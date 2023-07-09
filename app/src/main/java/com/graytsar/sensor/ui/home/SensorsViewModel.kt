package com.graytsar.sensor.ui.home

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.graytsar.sensor.model.UISensor
import com.graytsar.sensor.utils.Globals
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SensorsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sensorManager: SensorManager
) : ViewModel() {
    val sensors: List<UISensor> = Globals.sensors.filter {
        sensorManager.getDefaultSensor(it.sensorType) != null
    }
    private val sensorListeners: Map<Int, SensorEventListener> =
        sensors.associate { item: UISensor ->
            item.sensorType to if (item.axes == 1) {
                object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event == null) return
                        item.listener?.invoke(Triple(event.values[0], 0.0f, 0.0f))
                    }

                    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /* do nothing */
                    }
                }
        } else {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return
                    item.listener?.invoke(Triple(event.values[0], event.values[1], event.values[2]))
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /* do nothing */
                }
            }
        }
    }

    fun registerListeners() {
        sensorListeners.forEach {
            sensorManager.registerListener(
                it.value,
                sensorManager.getDefaultSensor(it.key),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun unregisterListeners() {
        sensorListeners.forEach {
            sensorManager.unregisterListener(it.value)
        }
        sensors.forEach {
            it.listener = null
        }
    }
}