package com.example.stairtree.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stairtree.background.SensorService
import com.example.stairtree.databinding.FragmentHomeBinding
import androidx.room.Room
import com.example.stairtree.db.AppDatabase
import com.example.stairtree.db.daily.DailyDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val model by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var dailyDatabase: DailyDao
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

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
        db = AppDatabase.create(requireContext())
        dailyDatabase = db.daily()
        coroutineScope.launch {
            val co2Emittion = dailyDatabase.selectStairSum() / 60000
            val co2Reduction = dailyDatabase.selectElevatorSum() / 60000
            if (co2Emittion > co2Reduction) {
                binding.usage.text = "木%,.2f本分の二酸化炭素排出".format(co2Emittion - co2Reduction)
            } else {
                binding.usage.text = "木%,.2f本分の二酸化炭素削減".format(co2Reduction - co2Emittion)

            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}