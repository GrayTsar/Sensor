package com.graytsar.sensor.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class UISensor(
    val sensorType: Int,
    val axes: Int,
    val dataPoints: Int,
    @StringRes
    val title: Int,
    @StringRes
    val unit: Int,
    @StringRes
    val info: Int,
    @ColorRes
    val color: Int,
    @DrawableRes
    val icon: Int
) {
    var listener: ((Triple<Float, Float, Float>) -> Unit)? = null
}