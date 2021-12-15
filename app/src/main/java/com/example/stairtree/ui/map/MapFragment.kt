package com.example.stairtree.ui.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
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
import com.example.stairtree.ui.map.detail.MapDetailObject
import com.google.android.gms.maps.*
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


class MapFragment : Fragment(), OnMapReadyCallback {

    private val model by viewModels<MapViewModel>()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val firebaseDb = Firebase.firestore
    private var nowCountryNumber: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        MapsInitializer.initialize(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.onCreate(savedInstanceState)
        mapFragment.getMapAsync(this@MapFragment)
        return binding.root
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this@MapFragment)
//        mapFragment.onCreate(savedInstanceState)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        firebaseDb.collection("global").document("global").get().addOnSuccessListener {
            val stairSum = it["stair"].toString().toDouble()
            val elevatorSum = it["elevator"].toString().toDouble()
            var nowCountry = MapDetailObject.level1Message[0]


            when {
                elevatorSum > stairSum * 2 -> {
                    binding.textView3.text = "地球温暖化レベル2"
                    var nowRatio = (elevatorSum -stairSum*2 )/ stairSum
                    if(nowRatio>=1)nowRatio = 0.999

                    binding.yabasa.apply {
                        max = 100
                        min = 0
                        progress = (nowRatio * 100).toInt()
                        progressDrawable.colorFilter = PorterDuffColorFilter(
                            Color.RED,
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                    nowCountryNumber = (nowRatio * MapDetailObject.level2size).toInt()
                    level2(googleMap)
                    nowCountry = MapDetailObject.level2Message[nowCountryNumber]
                }
                elevatorSum > stairSum -> {
                    var nowRatio = (elevatorSum -stairSum )/ stairSum
                    if(nowRatio>=1)nowRatio = 0.999

                    binding.yabasa.apply {
                        max = 100
                        min = 0
                        progress = (nowRatio * 100).toInt()
                        progressDrawable.colorFilter = PorterDuffColorFilter(
                            Color.RED,
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                    binding.textView3.text = "地球温暖化レベル1"
                    nowCountryNumber = (nowRatio * MapDetailObject.level1size).toInt()
                    level1(googleMap)
                    nowCountry = MapDetailObject.level1Message[nowCountryNumber]
                }
                else -> {
                    var nowRatio = elevatorSum  / stairSum
                    if(nowRatio>=1)nowRatio = 0.999

                    binding.yabasa.apply {
                        max = 100
                        min = 0
                        progress = (nowRatio * 100).toInt()
                        progressDrawable.colorFilter = PorterDuffColorFilter(
                            Color.RED,
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                    binding.textView3.text = "地球温暖化レベル0"
                }
            }

            Log.i("nowCountry", nowCountry.toString())
            moveMap(googleMap, nowCountry.latitude, nowCountry.longitude, 3F)
        }
    }

    private fun getCountryGeoJson(country: String): Int {
        return resources.getIdentifier(country, "raw", activity?.packageName)
    }

    private fun fillCountry(
        map: GoogleMap,
        country: Int,
        markerManager: MarkerManager,
        groundOverlayManager: GroundOverlayManager,
        polygonManager: PolygonManager,
        polylineManager: PolylineManager
    ): GeoJsonLayer {
        return GeoJsonLayer(
            map,
            country,
            context,
            markerManager,
            polygonManager,
            polylineManager,
            groundOverlayManager
        ).apply {
            defaultPolygonStyle.fillColor = Color.argb(100, 255, 0, 0)
            addLayerToMap()
        }
    }

    private fun setTyphoon(map: GoogleMap) {
        val newarkLatLng = LatLng(35.987261, 138.927622)
        val newarkMap = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.tayhoon))
            .position(newarkLatLng, 860000f, 650000f)
        map.addGroundOverlay(newarkMap)
    }

    private fun level1(googleMap: GoogleMap) {
        // GeoJSON polygon
        val markerManager = MarkerManager(googleMap)
        val groundOverlayManager = GroundOverlayManager(googleMap)
        val polygonManager = PolygonManager(googleMap)
        val polylineManager = PolylineManager(googleMap)
        for (i in 0..nowCountryNumber) {
            val mapMemo = MapDetailObject.level1Message[i]

            val json = getCountryGeoJson(mapMemo.countryJson)
            fillCountry(
                googleMap,
                json,
                markerManager,
                groundOverlayManager,
                polygonManager,
                polylineManager
            ).also {
                it.defaultPolygonStyle.fillColor = Color.RED
                it.defaultPointStyle.setPolygonFillColor(Color.RED)
            }.setOnFeatureClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(mapMemo.title)
                    setMessage(mapMemo.message)
                    setNegativeButton("詳細") { _, _ ->
                        val intent = Intent(activity, MapDetailActivity::class.java)
                        intent.putExtra("title", mapMemo.country)
                        intent.putExtra("message", mapMemo.message)
                        val intentWeb = Intent(Intent.ACTION_VIEW, Uri.parse(mapMemo.articleURL))
                        startActivity(intentWeb)
                    }
                    setPositiveButton("ok") { _, _ -> } // OK
                    create()
                    show()  // AlertDialogを表示
                }
            }
        }
    }

    private fun level2(googleMap: GoogleMap) {
        // GeoJSON polygon
        val markerManager = MarkerManager(googleMap)
        val groundOverlayManager = GroundOverlayManager(googleMap)
        val polygonManager = PolygonManager(googleMap)
        val polylineManager = PolylineManager(googleMap)

        MapDetailObject.level1Message.forEach { mapMemo ->
            val json = getCountryGeoJson(mapMemo.countryJson)
            fillCountry(
                googleMap,
                json,
                markerManager,
                groundOverlayManager,
                polygonManager,
                polylineManager
            ).setOnFeatureClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(mapMemo.title)
                    setMessage(mapMemo.message)
                    setNegativeButton("詳細") { _, _ ->
                        val intent = Intent(activity, MapDetailActivity::class.java)
                        intent.putExtra("title", mapMemo.country)
                        val intentWeb = Intent(Intent.ACTION_VIEW, Uri.parse(mapMemo.articleURL))
                        startActivity(intentWeb)
                    }
                    setPositiveButton("ok") { _, _ -> } // OK
                    create()
                    show() // AlertDialogを表示
                }
            }
        }
        for (i in 0..nowCountryNumber) {
            val mapMemo = MapDetailObject.level2Message[i]

            val json = getCountryGeoJson(mapMemo.countryJson)
            fillCountry(
                googleMap,
                json,
                markerManager,
                groundOverlayManager,
                polygonManager,
                polylineManager
            ).setOnFeatureClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(mapMemo.title)
                    setMessage(mapMemo.message)
                    setNegativeButton("詳細") { _, _ ->
                        val intent = Intent(activity, MapDetailActivity::class.java)
                        intent.putExtra("title", mapMemo.country)
                        intent.putExtra("message", mapMemo.message)
                        val intentWeb = Intent(Intent.ACTION_VIEW, Uri.parse(mapMemo.articleURL))
                        startActivity(intentWeb)
                    }
                    setPositiveButton("ok") { _, _ -> } // OK
                    create()
                    show()  // AlertDialogを表示
                }
            }
        }
//        setTyphoon(googleMap)
    }

    private fun moveMap(map: GoogleMap, latitude: Double, longitude: Double, zoom: Float) {
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