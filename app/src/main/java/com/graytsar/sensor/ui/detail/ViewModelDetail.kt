package com.graytsar.sensor.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelDetail : ViewModel() {
    val xValue = MutableLiveData("0")
    val yValue = MutableLiveData("0")
    val zValue = MutableLiveData("0")

    var unit: String = ""
    var name: String = ""
    var count: Int = 1

    var enableLog: Boolean = false
}