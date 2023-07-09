package com.graytsar.sensor.ui.detail

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.graytsar.sensor.repository.entity.SensorEntity
import com.graytsar.sensor.repository.repository.SensorRepository
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE
import com.graytsar.sensor.utils.Globals
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val sensorManager: SensorManager,
    private val sensorRepository: SensorRepository,
) : ViewModel() {

    val sensorType = savedStateHandle.get<Int>(ARG_SENSOR_TYPE)!!
    val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)!!
    val itemSensor = Globals.sensors.find {
        it.sensorType == sensorType
    }!!

    val xValues = arrayListOf<Float>()
    val yValues = arrayListOf<Float>()
    val zValues = arrayListOf<Float>()

    var singleUpdate: (() -> Unit)? = null
    var multiUpdate: (() -> Unit)? = null

    var enableLog: Boolean = false

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

    suspend fun insertSensor(): Long {
        return sensorRepository.insert(
            SensorEntity(
                id = 0,
                sensorType = sensorType,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}