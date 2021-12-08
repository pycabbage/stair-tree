package com.example.stairtree.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stairtree.background.SensorService
import com.example.stairtree.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val model by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.serviceButton.setOnClickListener {
            val serviceIntent = Intent(context, SensorService::class.java)
            startForegroundService(context!!, serviceIntent)
        }

        binding.stopButton.setOnClickListener {
            activity?.stopService(Intent(context, SensorService::class.java))
            Toast.makeText(context, "Sensor stopped", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}