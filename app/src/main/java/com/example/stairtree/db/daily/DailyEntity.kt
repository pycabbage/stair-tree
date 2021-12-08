package com.example.stairtree.db.daily

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily")
data class DailyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val date: String,
    val stair: Double,
    val elevator: Double,
)

data class dataTuple(
    @ColumnInfo(name = "stair") val stair: Double,
    @ColumnInfo(name = "elevator") val elevator: Double
)
