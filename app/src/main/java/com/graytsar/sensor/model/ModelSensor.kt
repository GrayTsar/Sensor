package com.graytsar.sensor.model

import androidx.lifecycle.MutableLiveData

class ModelSensor(
    val sensorType: Int,
    val title: String,
    val sensorValuesCount: Int,
    val unit: String
) {

    val xValue = MutableLiveData("0")
    val yValue = MutableLiveData("0")
    val zValue = MutableLiveData("0")
}