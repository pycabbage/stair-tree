package com.example.stairtree.db.daily

import androidx.room.*

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

    @Query("select stair,elevator from daily")
    fun selectStairAndEle(): List<dataTuple>
}
data class dataTuple(
    @ColumnInfo(name = "stair") val stair: Double,
    @ColumnInfo(name = "elevator") val elevator: Double
)
