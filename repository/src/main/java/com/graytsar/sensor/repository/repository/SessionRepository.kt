package com.graytsar.sensor.repository.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.graytsar.sensor.repository.dao.SessionDAO
import com.graytsar.sensor.repository.data.Record
import com.graytsar.sensor.repository.entity.SessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepository @Inject constructor(
    private val sessionDAO: SessionDAO
) {

    suspend fun insert(sensor: SessionEntity): Long {
        return sessionDAO.insert(sensor)
    }

    suspend fun deleteById(id: Long) {
        sessionDAO.delete(id)
    }

    fun getRecordsPaged(): Flow<PagingData<Record>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { sessionDAO.selectAllPaged() }
        ).flow
    }
}