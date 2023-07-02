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
class ViewModelSensors @Inject constructor(
    savedStateHandle: SavedStateHandle,
    sensorManager: SensorManager
) : ViewModel() {
    val sensors: List<UISensor> = Globals.sensors.filter {
        sensorManager.getDefaultSensor(it.sensorType) != null
    }
    val sensorListeners: Map<Int, SensorEventListener> = sensors.associate { item: UISensor ->
        item.sensorType to if (item.valuesCount == 1) {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return
                    item.values.tryEmit(Triple(event.values[0], 0.0f, 0.0f))
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /* do nothing */
                }
            }
        } else {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return
                    item.values.tryEmit(Triple(event.values[0], event.values[1], event.values[2]))
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /* do nothing */
                }
            }
        }
    }
}