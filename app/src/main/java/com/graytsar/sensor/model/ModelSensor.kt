package com.graytsar.sensor.model

import androidx.lifecycle.MutableLiveData

class ModelSensor(
    val sensorType: Int,
    val title: String,
    val sensorValuesCount: Int,
    val unit:String
) {

    val xValue = MutableLiveData("default")
    val yValue = MutableLiveData("default")
    val zValue = MutableLiveData("default")
}