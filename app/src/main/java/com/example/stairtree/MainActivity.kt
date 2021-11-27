package com.example.stairtree

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stairtree.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.serviceButton.setOnClickListener {
            val serviceIntent = Intent(this, SensorService::class.java)
            startForegroundService(serviceIntent)
        }
        binding.stopButton.setOnClickListener {
            stopService(Intent(applicationContext, SensorService::class.java))
            Toast.makeText(applicationContext, "Sensor stopped", Toast.LENGTH_SHORT).show()
        }
    }
}

