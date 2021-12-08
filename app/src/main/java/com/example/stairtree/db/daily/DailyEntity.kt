package com.example.stairtree.db.daily

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily")
data class DailyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val date: String,
    val stair: Double,
    val elevator: Double,
)