package com.graytsar.sensor.utils

import android.hardware.Sensor
import com.graytsar.sensor.R
import com.graytsar.sensor.model.UISensor

const val ARG_SENSOR_TYPE = "ARG_SENSOR_TYPE"
const val keyPreferenceTheme = "preferenceTheme"
const val keyTheme = "theme"

object Globals {
    var isNightMode: Boolean = false

    val sensors = listOf(
        UISensor(
            sensorType = Sensor.TYPE_ACCELEROMETER,
            valuesCount = 3,
            title = R.string.sensorAccelerometer,
            unit = R.string.unitAcceleration,
            info = R.string.infoAccelerometer,
            color = R.color.red,
            icon = R.drawable.ic_acceleration
        ),
        UISensor(
            sensorType = Sensor.TYPE_MAGNETIC_FIELD,
            valuesCount = 3,
            title = R.string.sensorMagneticField,
            unit = R.string.unitMagneticField,
            info = R.string.infoMagneticField,
            color = R.color.pink,
            icon = R.drawable.ic_magnet
        ),
        UISensor(
            sensorType = Sensor.TYPE_GRAVITY,
            valuesCount = 3,
            title = R.string.sensorGravity,
            unit = R.string.unitAcceleration,
            info = R.string.infoGravity,
            color = R.color.purple,
            icon = R.drawable.ic_gravity
        ),
        UISensor(
            sensorType = Sensor.TYPE_GYROSCOPE,
            valuesCount = 3,
            title = R.string.sensorGyroscope,
            unit = R.string.unitAngularVelocity,
            info = R.string.infoGyroscope,
            color = R.color.deep_blue,
            icon = R.drawable.ic_gyroscope
        ),
        UISensor(
            sensorType = Sensor.TYPE_LINEAR_ACCELERATION,
            valuesCount = 3,
            title = R.string.sensorLinearAcceleration,
            unit = R.string.unitAcceleration,
            info = R.string.infoLinearAcceleration,
            color = R.color.indigo,
            R.drawable.ic_linearacceleration
        ),
        UISensor(
            sensorType = Sensor.TYPE_AMBIENT_TEMPERATURE,
            valuesCount = 1,
            title = R.string.sensorAmbientTemperature,
            unit = R.string.unitTemperature,
            info = R.string.infoAmbientTemperature,
            color = R.color.blue,
            icon = R.drawable.ic_temperature
        ),
        UISensor(
            sensorType = Sensor.TYPE_LIGHT,
            valuesCount = 1,
            title = R.string.sensorLight,
            unit = R.string.unitIlluminance,
            info = R.string.infoLight,
            color = R.color.light_blue,
            icon = R.drawable.ic_light
        ),
        UISensor(
            sensorType = Sensor.TYPE_PRESSURE,
            valuesCount = 1,
            title = R.string.sensorPressure,
            unit = R.string.unitPressure,
            info = R.string.infoPressure,
            color = R.color.cyan,
            icon = R.drawable.ic_pressure
        ),
        UISensor(
            sensorType = Sensor.TYPE_RELATIVE_HUMIDITY,
            valuesCount = 1,
            title = R.string.sensorRelativeHumidity,
            unit = R.string.unitPercent,
            info = R.string.infoRelativeHumidity,
            color = R.color.teal,
            icon = R.drawable.ic_humidity
        ),
        UISensor(
            sensorType = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            valuesCount = 3,
            title = R.string.sensorGeomagneticRotationVector,
            unit = R.string.unitNone,
            info = R.string.infoGeomagneticRotationVector,
            color = R.color.green,
            icon = R.drawable.ic_rotate
        ),
        UISensor(
            sensorType = Sensor.TYPE_PROXIMITY,
            valuesCount = 1,
            title = R.string.sensorProximity,
            unit = R.string.unitProximity,
            info = R.string.infoProximity,
            color = R.color.light_green,
            icon = R.drawable.ic_proximity
        ),
        UISensor(
            sensorType = Sensor.TYPE_STEP_COUNTER,
            valuesCount = 1,
            title = R.string.sensorStepCounter,
            unit = R.string.unitStep,
            info = R.string.infoStepCounter,
            color = R.color.lime,
            icon = R.drawable.ic_steps
        )
    )
}