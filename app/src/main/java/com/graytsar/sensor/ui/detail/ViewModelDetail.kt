package com.graytsar.sensor.ui.detail

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE
import com.graytsar.sensor.utils.Globals
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelDetail @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val sensorManager: SensorManager
) : ViewModel() {

    val sensorType = savedStateHandle.get<Int>(ARG_SENSOR_TYPE)!!
    val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)!!
    val itemSensor = Globals.sensors.find {
        it.sensorType == sensorType
    }!!

    val csvHeader =
        "TIMESTAMP,X,Y,Z,NAME:${sensor.name},VENDOR:${sensor.vendor},VERSION:${sensor.version},POWER:${sensor.power}mA,MAXDELAY:${sensor.maxDelay},MINDELAY:${sensor.minDelay},MAXRANGE:${sensor.maximumRange}"
    var displayPoints: Float = 400f

    val xValue = MutableLiveData("0")
    val yValue = MutableLiveData("0")
    val zValue = MutableLiveData("0")

    var enableLog: Boolean = false
}