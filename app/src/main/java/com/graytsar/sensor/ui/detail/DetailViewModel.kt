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
    val xValues = arrayListOf<Float>()

    /**
     * y axis values.
     */
    val yValues = arrayListOf<Float>()

    /**
     * z axis values.
     */
    val zValues = arrayListOf<Float>()

    /**
     * Callbacks for updating the graph for single axis sensors.
     */
    var singleUpdate: (() -> Unit)? = null

    /**
     * Callbacks for updating the graph for multi axis sensors.
     */
    var multiUpdate: (() -> Unit)? = null

    /**
     * Current state of the recording session.
     */
    var isRecording: Boolean = false

    /**
     * A listener for sensor event. It will be called when the sensor value changes.
     * It will add the new value to the list of values and remove the first value if the list is too long.
     * Invokes [singleUpdate] or [multiUpdate] depending on the number of axes.
     */
    val sensorEventListener: SensorEventListener = when (itemSensor.axes) {
        1 -> object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (xValues.size >= itemSensor.dataPoints) {
                    xValues.removeFirstOrNull()
                }
                xValues.add(event.values[0])
                singleUpdate?.invoke()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                /* do nothing */
            }
        }

        else -> object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (xValues.size >= itemSensor.dataPoints) {
                    xValues.removeFirstOrNull()
                    yValues.removeFirstOrNull()
                    zValues.removeFirstOrNull()
                }
                xValues.add(event.values[0])
                yValues.add(event.values[1])
                zValues.add(event.values[2])
                multiUpdate?.invoke()
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