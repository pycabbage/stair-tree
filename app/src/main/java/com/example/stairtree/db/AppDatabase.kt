package com.example.stairtree.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stairtree.db.daily.DailyDao
import com.example.stairtree.db.daily.DailyEntity
import com.example.stairtree.db.sensor.SensorDao
import com.example.stairtree.db.sensor.SensorEntity

@Database(entities = [SensorEntity::class, DailyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensor(): SensorDao
    abstract fun daily(): DailyDao

    companion object {
        fun create(context: Context): AppDatabase = synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "data.db").build()
        }
    }
}
