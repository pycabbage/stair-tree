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
import com.example.stairtree.db.AppDatabase
import com.example.stairtree.db.daily.DailyDao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    private val firebaseDb = Firebase.firestore

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

        //binding.CO2.speed = 0.03 スピードを変更できる

        coroutineScope.launch {
            firebaseDb.collection("data").get().addOnSuccessListener {
                var stairSum = 0.0
                var elevatorSum = 0.0
                for (element in it) {
                    stairSum += element["stair"].toString().toDouble()
                    elevatorSum += element["elevator"].toString().toDouble()
                }
                stairSum /= 60000
                elevatorSum /= 60000
                if (stairSum > elevatorSum) {
                    binding.worldusing.text = "世界木%,.2f本分の二酸化炭素排出...".format(stairSum - elevatorSum)
                } else {
                    binding.worldusing.text = "世界木%,.2f本分の二酸化炭素削減!".format(elevatorSum - stairSum)
                }
            }
        }

        model.selectElevatorSum2.observe(viewLifecycleOwner) {
            val co2Emission = it.stair / 60000
            val co2Reduction = it.elevator / 60000
            if (co2Emission > co2Reduction) {
                binding.usage.text = "木%,.2f本分の二酸化炭素排出...".format(co2Emission - co2Reduction)
            } else {
                binding.usage.text = "木%,.2f本分の二酸化炭素削減!".format(co2Reduction - co2Emission)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
