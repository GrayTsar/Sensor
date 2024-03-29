package com.graytsar.sensor.ui.detail

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.graytsar.sensor.repository.entity.SessionEntity
import com.graytsar.sensor.repository.repository.SessionRepository
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE
import com.graytsar.sensor.utils.Globals
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject

/**
 * A ViewModel for the [DetailFragment].
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val sensorManager: SensorManager,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    /**
     * Type of the sensor to display.
     */
    val sensorType = savedStateHandle.get<Int>(ARG_SENSOR_TYPE)!!

    /**
     * Hardware sensor to display.
     */
    val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)!!

    /**
     * Info data about the sensor.
     */
    val itemSensor = Globals.sensors.find {
        it.sensorType == sensorType
    }!!

    /**
     * x axis values.
     */
    val xValues = ConcurrentLinkedQueue<Float>()

    /**
     * y axis values.
     */
    val yValues = ConcurrentLinkedQueue<Float>()

    /**
     * z axis values.
     */
    val zValues = ConcurrentLinkedQueue<Float>()

    /**
     * Current state of the recording session.
     */
    var isRecording: Boolean = false

    /**
     * A listener for sensor event. It will be called when the sensor value changes.
     * It will add the new value to the list of values and remove the first value if the list is too long.
     */
    val sensorEventListener: SensorEventListener = when (itemSensor.axes) {
        1 -> object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (xValues.size >= itemSensor.dataPoints) {
                    runCatching { xValues.remove() }
                }
                xValues.add(event.values[0])
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                /* do nothing */
            }
        }

        else -> object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (xValues.size >= itemSensor.dataPoints) {
                    runCatching {
                        xValues.remove()
                        yValues.remove()
                        zValues.remove()
                    }
                }
                xValues.add(event.values[0])
                yValues.add(event.values[1])
                zValues.add(event.values[2])
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                /* do nothing */
            }
        }
    }

    /**
     * Inserts a new recording session into the database.
     */
    suspend fun insertSensor(): Long {
        return sessionRepository.insert(
            SessionEntity(
                id = 0,
                sensorType = sensorType,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}