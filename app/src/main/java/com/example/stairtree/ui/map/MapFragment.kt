package com.example.stairtree.ui.map

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stairtree.R
import com.example.stairtree.databinding.FragmentMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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

        val country = getCountryGeoJson("usa")
        val greenLand = getCountryGeoJson("grl")
        fillCountry(googleMap,country)
        fillCountry(googleMap, greenLand).setOnFeatureClickListener { feature ->
                // BuilderからAlertDialogを作成
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("グリーンランドについて") // タイトル
                    .setMessage("グリーンランドは、\n" +
                            "2100年まで海面が60cm上昇といわれています。") // メッセージ
                    .setPositiveButton("ok") { dialog, which -> // OK
                        Toast.makeText(context, "OKがタップされた", Toast.LENGTH_SHORT).show()
                    }
                    .create()
                // AlertDialogを表示
                dialog.show()
        }
        setTyphoon(googleMap)
    }

    fun getCountryGeoJson(country: String): Int {
        return resources.getIdentifier(country, "raw", activity?.packageName)
    }

    fun fillCountry(map: GoogleMap, country: Int):GeoJsonLayer {
        val layer = GeoJsonLayer(map, country, context)
        layer.addLayerToMap()
        return layer
    }
    fun setTyphoon(map:GoogleMap){
        val newarkLatLng = LatLng(35.987261, 138.927622)
        val newarkMap = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.tayhoon))
            .position(newarkLatLng, 860000f, 650000f)
        map.addGroundOverlay(newarkMap)

    }

}