package com.graytsar.sensor.repository.di

import android.content.Context
import androidx.room.Room
import com.graytsar.sensor.repository.database.SensorDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide the database.
 */
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    /**
     * Provides the [SensorDatabase].
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SensorDatabase {
        return Room.databaseBuilder(context, SensorDatabase::class.java, "sensor_database").build()
    }

    /**
     * Provides the [com.graytsar.sensor.repository.dao.SessionDAO].
     */
    @Provides
    @Singleton
    fun provideSessionRepo(db: SensorDatabase) = db.sessionDao()

    /**
     * Provides the [com.graytsar.sensor.repository.dao.RecordDAO].
     */
    @Provides
    @Singleton
    fun provideRecordDao(db: SensorDatabase) = db.recordDao()
}