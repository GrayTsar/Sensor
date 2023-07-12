package com.graytsar.sensor.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for the recorded sensor event.
 */
@Entity(
    tableName = "record",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["record_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["record_id"])
    ]
)
data class RecordEntity(
    /**
     * Unique identifier for the record.
     */
    @PrimaryKey(autoGenerate = true) val id: Long,
    /**
     * Id of the recording session.
     */
    @ColumnInfo(name = "record_id") val recordId: Long,
    /**
     * Timestamp of the event created.
     */
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    /**
     * x axis value of the event.
     */
    @ColumnInfo(name = "x") val x: Float,
    /**
     * y axis value of the event.
     */
    @ColumnInfo(name = "y") val y: Float,
    /**
     * z axis value of the event.
     */
    @ColumnInfo(name = "z") val z: Float
)