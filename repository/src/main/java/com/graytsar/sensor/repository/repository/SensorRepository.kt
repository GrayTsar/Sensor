package com.graytsar.sensor.repository.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.graytsar.sensor.repository.dao.SensorDAO
import com.graytsar.sensor.repository.data.Record
import com.graytsar.sensor.repository.entity.SensorEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SensorRepository @Inject constructor(
    private val sensorDAO: SensorDAO
) {

    suspend fun insert(sensor: SensorEntity): Long {
        return sensorDAO.insert(sensor)
    }

    suspend fun deleteById(id: Long) {
        sensorDAO.delete(id)
    }

    fun getRecordsPaged(): Flow<PagingData<Record>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { sensorDAO.selectAllPaged() }
        ).flow
    }
}