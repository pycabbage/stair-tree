package com.example.stairtree.background

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
import com.example.stairtree.R
import com.example.stairtree.db.AppDatabase
import com.example.stairtree.db.daily.DailyDao
import com.example.stairtree.db.daily.DailyEntity
import com.example.stairtree.db.sensor.SensorDao
import com.example.stairtree.db.sensor.SensorEntity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class SensorService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var pressure: Sensor? = null
    private lateinit var db: AppDatabase
    private lateinit var sensorDatabase: SensorDao
    private lateinit var dailyDatabase: DailyDao
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val firebaseDb = Firebase.firestore
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

    private val upDownJadge = upAndDownJudgment(70)

    inner class betweenTime {
        var time1: LocalTime? = null
        var time2: LocalTime? = null
        fun start(time: LocalTime) {
            time1 = time
        }

        fun isStarted(): Boolean {
            return time1 != null
        }

        fun isEnd(): Boolean {
            return time2 != null
        }

        fun end(time: LocalTime) {
            time2 = time
        }

        fun between(): Long {
            return ChronoUnit.MILLIS.between(time1, time2)
        }

        fun reset() {
            time1 = null
            time2 = null

        }
    }

    inner class isElevator() {
        var data = mutableListOf<Double>()
        fun push(item: Double) {
            Log.i("pushedValue", item.toString())
            data.add(item)
        }

        fun jadge(): Boolean {
            val border = 0.04
            return data.find { abs(it) > border } != null
        }

        fun reset() {
            data = mutableListOf()
        }
    }

    val between = betweenTime()
    val isele = isElevator()
    override fun onSensorChanged(event: SensorEvent) {
//        val millibarsOfPressure = event.values[0] / 100000 + 1000 //これはエミュレーターで動かすときにいろいろやってたやつ
        val millibarsOfPressure = event.values[0]
        val time = LocalTime.now()
        upDownJadge.push(millibarsOfPressure.toDouble())
        val slope = if (upDownJadge.possibleToJudge()) {
            upDownJadge.sloop()
        } else {
            0.0
        }

        var bet = 0L
        var isEle = false
        val border = 0.019
        if (abs(slope) > border && !between.isStarted()) { //閾値を超え、かつまだスタート時間を取得していなかったら、
            Log.i("state", "閾値を超え、かつまだスタート時間を取得していない")
            between.start(time) //　スタートを設定
            isele.push(slope)
        } else if (abs(slope) <= border && between.isEnd()) { //閾値を超えておらず、かつ既に終了時間を取得していたら、
            Log.i("state", "閾値を超えておらず、かつ既に終了時間を取得している")
            bet = between.between()
            Log.i(
                "between",
                "between: " + between.between().toString() + " time: " + time.toString()
            )// 間の時間を出力
            var elevatorUsage = 0.0
            var stairUsage = 0.0
            if (isele.jadge()) {
                Log.i("elevater", "Yes")
                isEle = isele.jadge()
                elevatorUsage = bet.toDouble()
            } else {
                stairUsage = bet.toDouble()
            }
            val data = hashMapOf(
                "date" to LocalDate.now().toString(),
                "stair" to stairUsage,
                "elevator" to elevatorUsage
            )
            firebaseDb.collection("data").add(data)

            coroutineScope.launch {
                dailyDatabase.insert(
                    DailyEntity(
                        0,
                        LocalDate.now().toString(),
                        stairUsage,
                        elevatorUsage,
                    )
                )
            }
            between.reset() // リセット
            isele.reset()
        } else if (abs(slope) <= border && between.isStarted()) { //閾値を超えておらず、かつスタート時間を取得しているなら。
            Log.i("state", "閾値を超えておらず、かつスタート時間を取得している")
            isele.push(slope)
            between.end(time) // 終了時間を取得
        }

        if (between.isStarted()) {
            isele.push(slope)
        }

        Log.i("sample", millibarsOfPressure.toString())
        coroutineScope.launch {
            sensorDatabase.insert(
                SensorEntity(
                    time.toString(),
                    millibarsOfPressure,
                    slope,
                    bet,
                    isEle
                )
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.create(applicationContext)
        sensorDatabase = db.sensor()
        dailyDatabase = db.daily()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}