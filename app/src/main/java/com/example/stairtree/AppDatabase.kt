package com.example.stairtree

import android.content.Context
import androidx.room.*

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

@Entity(tableName = "sensor")
data class SensorEntity(
    @PrimaryKey val timeKey: String,
    val value: Float,
    val slope: Double,
    val between: Long,
)

@Entity(tableName = "daily")
data class DailyEntity(
    @PrimaryKey val date: String,
    val stair: Double,
    val elevator: Double,
)

@Dao
interface SensorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
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

@Dao
interface DailyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(database: DailyEntity)

    @Update
    fun update(database: DailyEntity)

    @Delete
    fun delete(database: DailyEntity)

    @Query("delete from daily")
    fun deleteAll()

    @Query("select * from daily")
    fun selectAll(): List<DailyEntity>
}