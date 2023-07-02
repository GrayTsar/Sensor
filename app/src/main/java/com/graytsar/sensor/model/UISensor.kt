package com.graytsar.sensor.model

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow

data class UISensor(
    val sensorType: Int,
    val valuesCount: Int,
    @StringRes
    val title: Int,
    @StringRes
    val unit: Int
) {
    /**
     * FIXME: this creates a new object on every sensor event. Causes a GC call every 1-2 minutes during testing.
     */
    val values = MutableStateFlow(Triple(0.0f, 0.0f, 0.0f))
}