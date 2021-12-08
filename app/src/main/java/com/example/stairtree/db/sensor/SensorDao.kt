package com.example.stairtree.db.sensor

import androidx.room.*

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