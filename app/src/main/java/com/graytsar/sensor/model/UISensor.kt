package com.graytsar.sensor.model

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData

class UISensor(
    val sensorType: Int,
    val valuesCount: Int,
    @StringRes
    val title: Int,
    @StringRes
    val unit: Int
) {

    val xValue = MutableLiveData("0")
    val yValue = MutableLiveData("0")
    val zValue = MutableLiveData("0")
}