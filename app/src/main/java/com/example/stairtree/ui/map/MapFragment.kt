package com.example.stairtree.ui.map

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stairtree.R
import com.example.stairtree.databinding.FragmentMapBinding
import com.example.stairtree.ui.map.detail.MapDetailActivity
import com.example.stairtree.ui.map.detail.MapDetailEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MapFragment : Fragment(), OnMapReadyCallback {

    private val model by viewModels<MapViewModel>()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val firebaseDb = Firebase.firestore
    private var nowCountryNumber: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        coroutineScope.launch {
            firebaseDb.collection("data").get().addOnSuccessListener {
                var stairSum = 0.0
                var elevatorSum = 0.0
                for (element in it) {
                    stairSum += element["stair"].toString().toDouble()
                    elevatorSum += element["elevator"].toString().toDouble()
                }

                binding.yabasa.max = 100
                binding.yabasa.min = 0
                binding.yabasa.progress = (stairSum % elevatorSum / elevatorSum * 100).toInt()
                binding.yabasa.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                var nowCountry = level1Message[0]
                if (elevatorSum > stairSum * 2) {
                    binding.textView3.text = "地球温暖化レベル2"
                    nowCountryNumber =
                        (stairSum % elevatorSum / elevatorSum * level2Message.size).toInt()

                    level2(googleMap)
                    nowCountry = level2Message[nowCountryNumber]
                } else if (elevatorSum > stairSum) {
                    binding.textView3.text = "地球温暖化レベル1"
                    nowCountryNumber =
                        (stairSum % elevatorSum / elevatorSum * level1Message.size).toInt()

                    level1(googleMap)
                    nowCountry = level1Message[nowCountryNumber]
                } else {
                    binding.textView3.text = "地球温暖化レベル0"
                }
                Log.i("nowCountry", nowCountry.toString())
                moveMap(googleMap, nowCountry.latitude, nowCountry.longitude, 3F)

            }
        }


    }

    private fun getCountryGeoJson(country: String): Int {
        return resources.getIdentifier(country, "raw", activity?.packageName)
    }

    fun fillCountry(
        map: GoogleMap,
        country: Int,
        markerManager: MarkerManager,
        groundOverlayManager: GroundOverlayManager,
        polygonManager: PolygonManager,
        polylineManager: PolylineManager
    ): GeoJsonLayer {
        val layer = GeoJsonLayer(
            map,
            country,
            context,
            markerManager,
            polygonManager,
            polylineManager,
            groundOverlayManager
        )

        layer.addLayerToMap()
        return layer
    }

    fun setTyphoon(map: GoogleMap) {
        val newarkLatLng = LatLng(35.987261, 138.927622)
        val newarkMap = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.tayhoon))
            .position(newarkLatLng, 860000f, 650000f)
        map.addGroundOverlay(newarkMap)

    }

    companion object {
        val level1Message = listOf(
            MapDetailEntity(
                country = "アメリカ",
                title = "アメリカで発生した地球温暖化の事例",
                message = "アメリカのフロリダ半島に巨大が上陸、その後ルイジアナ州に再上陸、大きな被害をもたらしました。",
                countryJson = "usa",
                articleURL = "https://www.bbc.com/japanese/50384396",
                latitude = -95.7128,
                longitude = 37.0902
            ),
            MapDetailEntity(
                country = "ツバル",
                title = "ツバル",
                message = "",
                countryJson = "tuv",
                articleURL = "https://www3.nhk.or.jp/news/html/20211110/k10013341181000.html",
                latitude = -8.516667,
                longitude = 179.216667
            ),
            MapDetailEntity(
                country = "バングラデシュ",
                title = "バングラデシュ",
                message = "地球温暖化によるサイクロンによって、多くの死者が出ています",
                countryJson = "bgd",
                articleURL = "https://www.unicef.or.jp/news/2019/0057.html",
                latitude = 22.7,
                longitude = 90.35,
            )
        )
        val level2Message = listOf(
            MapDetailEntity(
                country = "グリーンランド",
                title = "グリーンランドについて",
                message = " グリーンランドは、2100年までに海面が60cm上昇するといわれています。",
                countryJson = "grl",
                articleURL = "https://style.nikkei.com/article/DGXMZO64709280X01C20A0000000/",
                latitude = 71.7069,
                longitude = -42.6043
            ),
            MapDetailEntity(
                country = "日本",
                title = "日本について",
                message = "日本は100年後スーパー台風ってのがめっちゃ増えます",
                countryJson = "jpn",
                articleURL = "https://www.nikkei.com/article/DGXNASDG29034_Z20C12A5CR8000/",
                latitude = 24.0,
                longitude = 153.0
            )
        )
    }

    fun level1(googleMap: GoogleMap) {

        // GeoJSON polygon
        val markerManager = MarkerManager(googleMap)
        val groundOverlayManager = GroundOverlayManager(googleMap)
        val polygonManager = PolygonManager(googleMap)
        val polylineManager = PolylineManager(googleMap)

        level1Message.forEach { mapMemo ->
            val json = getCountryGeoJson(mapMemo.countryJson)
            fillCountry(
                googleMap,
                json,
                markerManager,
                groundOverlayManager,
                polygonManager,
                polylineManager
            ).setOnFeatureClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(mapMemo.title)
                    .setMessage(mapMemo.message)
                    .setNegativeButton("詳細") { _, _ ->
                        val intent = Intent(activity, MapDetailActivity::class.java)
                        intent.putExtra("title", mapMemo.country)
                        intent.putExtra(
                            "message",
                            mapMemo.message
                        )
                        val intentWeb = Intent(Intent.ACTION_VIEW, Uri.parse(mapMemo.articleURL))
                        startActivity(intentWeb)
                    }
                    .setPositiveButton("ok") { _, _ -> // OK
                    }
                    .create()
                // AlertDialogを表示
                dialog.show()
            }
        }
    }

    fun level2(googleMap: GoogleMap) {

        // GeoJSON polygon
        val markerManager = MarkerManager(googleMap)
        val groundOverlayManager = GroundOverlayManager(googleMap)
        val polygonManager = PolygonManager(googleMap)
        val polylineManager = PolylineManager(googleMap)

        level2Message.forEach { mapMemo ->
            val json = getCountryGeoJson(mapMemo.countryJson)
            fillCountry(
                googleMap,
                json,
                markerManager,
                groundOverlayManager,
                polygonManager,
                polylineManager
            ).setOnFeatureClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(mapMemo.title)
                    .setMessage(mapMemo.message)
                    .setNegativeButton("詳細") { _, _ ->
                        val intent = Intent(activity, MapDetailActivity::class.java)
                        intent.putExtra("title", mapMemo.country)
                        intent.putExtra(
                            "message",
                            mapMemo.message
                        )
                        startActivity(intent)
                    }
                    .setPositiveButton("ok") { _, _ -> // OK
                    }
                    .create()
                // AlertDialogを表示
                dialog.show()
            }
        }

        setTyphoon(googleMap)
    }

    fun moveMap(map: GoogleMap, latitude: Double, longitude: Double, zoom: Float = 1f) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ),
                zoom
            )
        )
    }

}