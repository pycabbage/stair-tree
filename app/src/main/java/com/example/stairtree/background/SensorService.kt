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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.stairtree.R
import com.example.stairtree.db.AppDatabase
import com.example.stairtree.db.daily.DailyDao
import com.example.stairtree.db.daily.DailyEntity
import com.example.stairtree.db.sensor.SensorDao
import com.example.stairtree.db.sensor.SensorEntity
import com.example.stairtree.ui.map.detail.MapDetailObject
import com.google.firebase.firestore.FieldValue
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
    private var step: Sensor? = null
    private lateinit var db: AppDatabase
    private lateinit var sensorDatabase: SensorDao
    private lateinit var dailyDatabase: DailyDao
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val firebaseDb = Firebase.firestore
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
            setSmallIcon(R.drawable.ki)
            addAction(
                R.drawable.ki, "stop",
                stopPendingIntent
            )
        }.build()

        firebaseDb.collection("global").document("global").addSnapshotListener { value, error ->
            if (error == null) {
                val stairSum = value!!.data!!["stair"].toString().toDouble()
                val elevatorSum = value.data!!["elevator"].toString().toDouble()
                val nowRatio = elevatorSum % stairSum / elevatorSum
                val nowCountryNumber1 = (nowRatio * MapDetailObject.level1size).toInt()
                val nowCountryNumber2 = (nowRatio * MapDetailObject.level2size).toInt()

                when {
                    elevatorSum > stairSum * 2 -> {
                        val mapObj = MapDetailObject.level2Message[nowCountryNumber2]

                        val sharedPref =
                            applicationContext.getSharedPreferences("country", Context.MODE_PRIVATE)
                        if (!sharedPref.getBoolean(mapObj.country, false)) {
                            sharedPref.edit().putBoolean(mapObj.country, true).apply()
                            //通知
                            (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also {
                                it.createNotificationChannel(
                                    NotificationChannel(
                                        "id",
                                        "${mapObj.country}が滅びました",
                                        NotificationManager.IMPORTANCE_DEFAULT
                                    )
                                )
                                it.notify(
                                    mapObj.longitude.toInt(),
                                    NotificationCompat.Builder(applicationContext, "id").apply {
                                        setSmallIcon(R.drawable.ki)
                                        setContentTitle("${mapObj.country}が滅びました")
                                        setAutoCancel(true)
                                    }.build()
                                )
                            }
                        }
                    }
                    elevatorSum > stairSum -> {
                        val mapObj = MapDetailObject.level1Message[nowCountryNumber1]

                        val sharedPref =
                            applicationContext.getSharedPreferences("country", Context.MODE_PRIVATE)
                        if (!sharedPref.getBoolean(mapObj.country, false)) {
                            sharedPref.edit().putBoolean(mapObj.country, true).apply()

                            //通知
                            (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also {
                                it.createNotificationChannel(
                                    NotificationChannel(
                                        "id",
                                        "${mapObj.country}が滅びました",
                                        NotificationManager.IMPORTANCE_DEFAULT
                                    )
                                )
                                it.notify(
                                    mapObj.longitude.toInt(),
                                    NotificationCompat.Builder(applicationContext, "id").apply {
                                        setSmallIcon(R.drawable.ki)
                                        setContentTitle("${mapObj.country}が滅びました")
                                        setAutoCancel(true)
                                    }.build()
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
//        coroutineScope.launch {
//            delay(5000)
//            val notification2 = NotificationCompat.Builder(applicationContext, id).apply {
//                setContentTitle("センサー")
//                setContentText("計測中じゃないよ")
//                setSmallIcon(R.drawable.ic_launcher_foreground)
//                addAction(
//                    R.drawable.ic_launcher_foreground, "stop",
//                    stopPendingIntent
//                )
//            }.build()
//            startForeground(1, notification2)
//        }

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    private val upDownJadge = UpAndDownJudgment(70)

    inner class BetweenTime {
        private var time1: LocalTime? = null
        private var time2: LocalTime? = null
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

    inner class IsElevator {
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

    val between = BetweenTime()
    val isele = IsElevator()
    override fun onSensorChanged(event: SensorEvent) {
//        val millibarsOfPressure = event.values[0] / 100000 + 1000 //これはエミュレーターで動かすときにいろいろやってたやつ
        val millibarsOfPressure = event.values[0]
        Log.i("aaaaa", event.sensor.name)
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
                val text = "エレベーターの利用を検知しました"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()

            } else {
                stairUsage = bet.toDouble()
                val text = "階段の利用を検知しました"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()

            }
            val data = hashMapOf(
                "date" to LocalDate.now().toString(),
                "stair" to stairUsage,
                "elevator" to elevatorUsage
            )

            firebaseDb.collection("data").add(data)
            val global = firebaseDb.collection("global")
                .document("global")

            global.update("stair", FieldValue.increment(stairUsage))
            global.update("elevator", FieldValue.increment(elevatorUsage))

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
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL)
        step = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        sensorManager.registerListener(this, step, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}