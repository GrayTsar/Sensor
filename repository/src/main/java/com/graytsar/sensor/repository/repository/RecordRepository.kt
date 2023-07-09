package com.graytsar.sensor.repository.repository

import com.graytsar.sensor.repository.dao.RecordDAO
import com.graytsar.sensor.repository.entity.RecordEntity
import javax.inject.Inject

class RecordRepository @Inject constructor(
    private val recordDAO: RecordDAO
) {

    suspend fun insert(records: List<RecordEntity>) {
        recordDAO.insertAll(records)
    }
}