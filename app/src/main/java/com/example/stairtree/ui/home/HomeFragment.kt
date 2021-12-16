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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private val model by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
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

        firebaseDb.collection("global").document("global").get().addOnSuccessListener {
            var stairSum = it["stair"].toString().toDouble()
            var elevatorSum = it["elevator"].toString().toDouble()
            stairSum /= 60000
            elevatorSum /= 60000
            if (elevatorSum > stairSum) {
                binding.worldusing.text = "世界:木%,.2f本分の二酸化炭素排出...".format(elevatorSum - stairSum)
            } else {
                binding.worldusing.text = "世界:木%,.2f本分の二酸化炭素削減!".format(stairSum - elevatorSum)
            }
        }

        model.selectElevatorSum2.observe(viewLifecycleOwner) {
            val co2Emission = it.elevator / 60000
            val co2Reduction = it.stair / 60000
            if (co2Emission > co2Reduction) {
                binding.usage.text = "個人:木%,.2f本分の二酸化炭素排出...".format(co2Emission - co2Reduction)
                binding.treeimage.setImageResource(R.drawable.kareta_ki)
            } else {
                binding.usage.text = "個人:木%,.2f本分の二酸化炭素削減!".format(co2Reduction - co2Emission)
                binding.treeimage.setImageResource(R.drawable.ki)
            }
        }

//        (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also {
//            it.createNotificationChannel(
//                NotificationChannel(
//                    "id",
//                    "title",
//                    NotificationManager.IMPORTANCE_DEFAULT
//                )
//            )
//            it.notify(10, NotificationCompat.Builder(context!!, "id").apply {
//                setSmallIcon(R.drawable.ki)
//                setContentTitle("title")
//                setContentText("message")
//                setAutoCancel(true)
//            }.build())
//        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
