package com.example.stairtree

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

class SensorService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var pressure: Sensor? = null
    private lateinit var db: AppDatabase
    private lateinit var sensorDatabase: SensorDao
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = "sensor foreground"
        val name = "sensor setting"

        if (manager.getNotificationChannel(id) == null) {
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(mChannel)
        }

        val stopIntent = Intent(this, SensorReceiver::class.java)

        val stopPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val notification = NotificationCompat.Builder(this, id).apply {
            setContentTitle("センサー")
            setContentText("計測中")
            setSmallIcon(R.drawable.ic_launcher_background)
            addAction(
                R.drawable.ic_launcher_foreground, "stop",
                stopPendingIntent
            )
        }.build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        val millibarsOfPressure = event.values[0]
        Log.i("sample", millibarsOfPressure.toString())
        coroutineScope.launch {
            sensorDatabase.insert(SensorEntity(LocalTime.now().toString(), millibarsOfPressure))
        }
    }

    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.create(applicationContext)
        sensorDatabase = db.sensor()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        db.close()
    }
}