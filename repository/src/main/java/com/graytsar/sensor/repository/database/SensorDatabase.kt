package com.graytsar.sensor.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.graytsar.sensor.repository.dao.RecordDAO
import com.graytsar.sensor.repository.dao.SensorDAO
import com.graytsar.sensor.repository.entity.RecordEntity
import com.graytsar.sensor.repository.entity.SensorEntity

@Database(
    entities = [
        SensorEntity::class,
        RecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SensorDatabase : RoomDatabase() {
    abstract fun sensorDao(): SensorDAO
    abstract fun recordDao(): RecordDAO
}