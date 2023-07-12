package com.graytsar.sensor.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.graytsar.sensor.repository.entity.RecordEntity

@Dao
interface RecordDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<RecordEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: RecordEntity)

    @Delete
    suspend fun delete(entity: RecordEntity)

    @Query("DELETE FROM record")
    suspend fun deleteAll()

    @Query("SELECT * FROM record WHERE record_id = :recordId LIMIT :limit OFFSET :offset")
    suspend fun selectByRecordingLimit(recordId: Long, limit: Int, offset: Int): List<RecordEntity>
}