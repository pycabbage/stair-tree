package com.example.stairtree

import android.content.Context
import androidx.room.*

@Database(entities = [SampleEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sample() : SampleDao

    companion object {
        fun create(context: Context): AppDatabase = synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "sample.db").build()
        }
    }
}

@Entity(tableName = "sample")
data class SampleEntity(
    @PrimaryKey val id: Int,
    val name: String,
)

@Dao
interface SampleDao {
    @Insert
    fun insert(database: SampleEntity)

    @Update
    fun update(database: SampleEntity)

    @Delete
    fun delete(database: SampleEntity)

    @Query("delete from sample")
    fun deleteAll()

    @Query("select * from sample")
    fun selectAll(): List<SampleEntity>
}
