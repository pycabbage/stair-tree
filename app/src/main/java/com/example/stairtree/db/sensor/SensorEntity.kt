package com.example.stairtree.db.sensor

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor")
data class SensorEntity(
    @PrimaryKey val timeKey: String,
    val value: Float,
    val slope: Double,
    val between: Long,
    val isele: Boolean,
)


