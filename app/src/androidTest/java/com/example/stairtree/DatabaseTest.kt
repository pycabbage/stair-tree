package com.example.stairtree

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var sensorDao: SensorDao
    private lateinit var dailyDao: DailyDao

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        synchronized(this) {
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "test.db"
            ).build()
        }
        sensorDao = db.sensor()
        dailyDao = db.daily()
    }

    @After
    @Throws(IOException::class)
    fun after() {
        db.close()
    }

    private fun reset() {
        sensorDao.deleteAll()
        dailyDao.deleteAll()
    }

    @Test
    @Throws(Exception::class)
    fun emptyTest() {
        reset()
        val emptySensorList = listOf<SensorEntity>()
        val emptyDailyList = listOf<DailyEntity>()
        assertEquals(sensorDao.selectAll(), emptySensorList)
        assertEquals(dailyDao.selectAll(), emptyDailyList)
    }

    @Test
    @Throws(Exception::class)
    fun aTest() {
        reset()
        sensorDao.insert(SensorEntity("a", 1f, 1.0, 1))
        dailyDao.insert(DailyEntity("a", 1.0, 1.0))
        val aSensorList = listOf(SensorEntity("a", 1f, 1.0, 1))
        val aDailyList = listOf(DailyEntity("a", 1.0, 1.0))
        assertEquals(sensorDao.selectAll(), aSensorList)
        assertEquals(dailyDao.selectAll(), aDailyList)
    }

    @Test
    @Throws(Exception::class)
    fun bTest() {
        reset()
        repeat(10) {
            sensorDao.insert(SensorEntity("a$it", 1f, 1.0, 1))
            dailyDao.insert(DailyEntity("a$it", 1.0, 1.0))
        }
        assertEquals(sensorDao.selectAll().size, 10)
        assertEquals(dailyDao.selectAll().size, 10)
    }

    @Test
    @Throws(Exception::class)
    fun cTest() {
        reset()
        repeat(10) {
            sensorDao.insert(SensorEntity("c", 1f, 1.0, it.toLong()))
            dailyDao.insert(DailyEntity("c", it.toDouble(), 1.0))
        }
        assertEquals(sensorDao.selectAll().size, 1)
        assertEquals(dailyDao.selectAll().size, 1)
    }

    @Test
    @Throws(Exception::class)
    fun dTest() {
        reset()
        repeat(10) {
            sensorDao.insert(SensorEntity("d$it", 1f, 1.0, 1))
            dailyDao.insert(DailyEntity("d$it", 1.0, 1.0))
        }
        repeat(5) {
            sensorDao.delete(SensorEntity("d$it", 1f, 1.0, 1))
            dailyDao.delete(DailyEntity("d$it", 1.0, 1.0))
        }
        assertEquals(sensorDao.selectAll().size, 5)
        assertEquals(dailyDao.selectAll().size, 5)
    }
}