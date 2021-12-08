package com.example.stairtree.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stairtree.R
import com.example.stairtree.SensorService
import com.example.stairtree.databinding.FragmentHomeBinding
import com.google.android.gms.maps.OnMapReadyCallback

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.serviceButton.setOnClickListener {
            val serviceIntent = Intent(context, SensorService::class.java)
            context?.let { it1 -> startForegroundService(it1, serviceIntent) }
        }
        binding.stopButton.setOnClickListener {
            activity?.stopService(Intent(context, SensorService::class.java))
            Toast.makeText(context, "Sensor stopped", Toast.LENGTH_SHORT).show()
        }
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}