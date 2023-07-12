package com.graytsar.sensor.repository.data

import androidx.room.ColumnInfo

/**
 * Relation data class to the recording session and its events.
 */
data class Record(
    /**
     * Id if the recording session.
     */
    @ColumnInfo(name = "id") val id: Long,
    /**
     * Type of the sensor that was recorded.
     */
    @ColumnInfo(name = "sensor_type") val sensorType: Int,
    /**
     * Timestamp of when the recording session started.
     */
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    /**
     * Number of events recorded.
     */
    @ColumnInfo(name = "count") val count: Int
)
