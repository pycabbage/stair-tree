package com.example.stairtree.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stairtree.R
import com.example.stairtree.databinding.FragmentDashboardBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer

class DashboardFragment : Fragment(), OnMapReadyCallback {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val mapFragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
        val country = getCountryGeoJson("usa")
        fillCountry(googleMap, country)
    }

    fun getCountryGeoJson(country: String): Int {
        return resources.getIdentifier(country, "raw", activity?.packageName)

    }

    fun fillCountry(map: GoogleMap, country: Int) {
        val layer = GeoJsonLayer(map, country, activity)
        layer.addLayerToMap()
    }
}