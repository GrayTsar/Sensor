package com.graytsar.sensor.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor")
data class SensorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "sensor_type") val sensorType: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)
