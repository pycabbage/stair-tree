package com.example.stairtree.ui.map

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stairtree.R
import com.example.stairtree.databinding.FragmentMapBinding
import com.example.stairtree.ui.map.detail.MapDetailActivity
import com.example.stairtree.ui.map.detail.MapDetailEntity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.geojson.GeoJsonLayer

class MapFragment : Fragment(), OnMapReadyCallback {

    private val model by viewModels<MapViewModel>()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

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

        level1(googleMap)
        // level2(googleMap)

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
                message = "アメリカのフロリダ半島に巨大ハリケーンカトリーナが上陸、その後ルイジアナ州に再上陸、大きな被害をもたらしました。",
                countryJson = "usa"
            ),
            MapDetailEntity(
                country = "ツバル",
                title = "ツバル",
                message = "",
                countryJson = "tuv"
            ),
            MapDetailEntity(
                country = "バングラデシュ",
                title = "バングラデシュ",
                message = "地球温暖化によるサイクロンによって、多くの死者が出ています",
                countryJson = "bgd"
            )
        )
        val level2Message = listOf(
            MapDetailEntity(
                country = "グリーンランド",
                title = "グリーンランドについて",
                message = " \"グリーンランドは、\\n\" +\n" +
                        "                            \"2100年までに海面が60cm上昇するといわれています。\"",
                countryJson = "grl"
            ),
            MapDetailEntity(
                country = "日本",
                title = "日本について",
                message = "日本は100年後スーパー台風ってのがめっちゃ増えます",
                countryJson = "jpn"
            )
        )
    }

    fun level1(googleMap: GoogleMap) {
        binding.yabasa.max = 100
        binding.yabasa.min = 0
        binding.yabasa.progress = 30
        binding.yabasa.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        binding.textView3.text = "地球温暖化レベル1"

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
                        startActivity(intent)
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
        binding.yabasa.max = 100
        binding.yabasa.min = 0
        binding.yabasa.progress = 30
        binding.yabasa.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        binding.textView3.text = "地球温暖化レベル2"
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

}