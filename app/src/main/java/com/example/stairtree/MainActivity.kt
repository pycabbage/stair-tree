package com.example.stairtree

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.stairtree.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var db: AppDatabase
    private lateinit var sensorDatabase: SensorDao
    private lateinit var sensorManager: SensorManager
    private var pressure: Sensor? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        db = AppDatabase.create(applicationContext)
        sensorDatabase = db.sensor()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
//        coroutineScope.launch {
//            repeat(100) {
//                sensorDatabase.insert(SensorEntity(LocalTime.now().toString(), it.toFloat()))
//            }
//        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            binding.textView.text = ""
        } else {
            binding.textView.text = "センサーない"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        val millibarsOfPressure = event.values[0]
        Log.i("label", millibarsOfPressure.toString())
        binding.textView.text = millibarsOfPressure.toString()
        coroutineScope.launch {
            sensorDatabase.insert(SensorEntity(LocalTime.now().toString(), millibarsOfPressure))
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        coroutineScope.launch {
            sensorDatabase.deleteAll()
            db.close()
        }
    }
}

