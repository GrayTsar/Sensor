package com.graytsar.sensor.repository.data

import androidx.room.ColumnInfo

data class Record(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "sensor_type") val sensorType: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "count") val count: Int
)
