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

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SensorDatabase {
        return Room.databaseBuilder(context, SensorDatabase::class.java, "sensor_database").build()
    }

    @Provides
    @Singleton
    fun provideSensorRepo(db: SensorDatabase) = db.sensorDao()

    @Provides
    @Singleton
    fun provideRecordDao(db: SensorDatabase) = db.recordDao()
}