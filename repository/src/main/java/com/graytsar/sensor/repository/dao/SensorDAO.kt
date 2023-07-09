package com.graytsar.sensor.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.graytsar.sensor.repository.entity.SensorEntity

@Dao
interface SensorDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SensorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SensorEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: SensorEntity)

    @Delete
    suspend fun delete(entity: SensorEntity)

    @Query("SELECT * FROM sensor")
    suspend fun selectAll(): List<SensorEntity>
}