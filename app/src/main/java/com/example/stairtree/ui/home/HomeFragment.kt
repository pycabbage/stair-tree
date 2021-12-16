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
import com.example.stairtree.R
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
            val serviceIntent = Intent(requireContext(), SensorService::class.java)
            startForegroundService(requireContext(), serviceIntent)
        }

        binding.stopButton.setOnClickListener {
            val stopIntent = Intent(requireContext(), SensorService::class.java)
            activity?.stopService(stopIntent)
            Toast.makeText(requireContext(), "Sensor stopped", Toast.LENGTH_SHORT).show()
        }

        model.selectSum.observe(viewLifecycleOwner) {
            binding.usage.text = if (it.elevator > it.stair) {
                binding.treeimage.setImageResource(R.drawable.kareta_ki)
                "個人:木%,.2f本分の二酸化炭素排出...".format(it.elevator - it.stair)
            } else {
                binding.treeimage.setImageResource(R.drawable.ki)
                "個人:木%,.2f本分の二酸化炭素削減!".format(it.stair - it.elevator)
            }
        }

        model.selectWorldSum.observe(viewLifecycleOwner) {
            binding.worldusing.text = if (it.elevator > it.stair) {
                "世界:木%,.2f本分の二酸化炭素排出...".format(it.elevator - it.stair)
            } else {
                "世界:木%,.2f本分の二酸化炭素削減!".format(it.stair - it.elevator)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
