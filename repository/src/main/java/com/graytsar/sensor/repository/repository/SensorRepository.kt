package com.graytsar.sensor.repository.repository

import com.graytsar.sensor.repository.dao.SensorDAO
import com.graytsar.sensor.repository.entity.SensorEntity
import javax.inject.Inject

class SensorRepository @Inject constructor(
    private val sensorDAO: SensorDAO
) {

    suspend fun insert(sensor: SensorEntity): Long {
        return sensorDAO.insert(sensor)
    }
}