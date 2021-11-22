package com.example.stairtree

import android.content.Context
import androidx.room.*

@Database(entities = [SensorEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensor() : SensorDao

    companion object {
        fun create(context: Context): AppDatabase = synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "sensor.db").build()
        }
    }
}

@Entity(tableName = "sensor")
data class SensorEntity(
    @PrimaryKey val timeKey: String,
    val value: Float,
)

@Dao
interface SensorDao {
    @Insert
    fun insert(database: SensorEntity)

    @Update
    fun update(database: SensorEntity)

    @Delete
    fun delete(database: SensorEntity)

    @Query("delete from sensor")
    fun deleteAll()

    @Query("select * from sensor")
    fun selectAll(): List<SensorEntity>
}
