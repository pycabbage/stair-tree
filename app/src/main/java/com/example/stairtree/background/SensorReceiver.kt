package com.example.stairtree.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SensorReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.stopService(Intent(context, SensorService::class.java))
        Toast.makeText(context, "Sensor stopped", Toast.LENGTH_SHORT).show()
    }
}
