package com.example.stairtree.db.daily

import androidx.room.*
import kotlinx.coroutines.flow.Flow

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

    @Query("select sum(stair) from daily")
    fun selectStairSum(): Double

    @Query("select sum(elevator) from daily")
    fun selectElevatorSum(): Double

    @Query("select sum(stair) as stair,sum(elevator) as elevator from daily")
    fun selectSum(): Flow<DataTuple>
}
