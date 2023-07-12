package com.graytsar.sensor.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.graytsar.sensor.repository.dao.RecordDAO
import com.graytsar.sensor.repository.dao.SessionDAO
import com.graytsar.sensor.repository.entity.RecordEntity
import com.graytsar.sensor.repository.entity.SessionEntity

@Database(
    entities = [
        SessionEntity::class,
        RecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
/**
 * The Room database for this app
 */
abstract class SensorDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDAO
    abstract fun recordDao(): RecordDAO
}