package com.graytsar.sensor.ui.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import com.graytsar.sensor.model.UISensor
import com.graytsar.sensor.utils.Globals
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the [SensorsFragment].
 */
@HiltViewModel
class SensorsViewModel @Inject constructor(
    private val sensorManager: SensorManager
) : ViewModel() {
    /**
     * List of sensors that are available on the device.
     */
    val sensors: List<UISensor> = Globals.sensors.filter {
        sensorManager.getDefaultSensor(it.sensorType) != null
    }

    /**
     * Map of sensor types to their event listeners.
     */
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
                        item.listener?.invoke(
                            Triple(
                                event.values[0],
                                event.values[1],
                                event.values[2]
                            )
                        )
                    }

                    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /* do nothing */
                    }
                }
            }
        }

    /**
     * Register all the listeners for the sensors.
     */
    fun registerListeners() {
        sensorListeners.forEach {
            sensorManager.registerListener(
                it.value,
                sensorManager.getDefaultSensor(it.key),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    /**
     * Unregister all the listeners for the sensors.
     */
    fun unregisterListeners() {
        sensorListeners.forEach {
            sensorManager.unregisterListener(it.value)
        }
        sensors.forEach {
            it.listener = null
        }
    }
}