package com.graytsar.sensor.ui.home

import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.sensor.R
import com.graytsar.sensor.databinding.ItemSensorBinding
import com.graytsar.sensor.model.UISensor
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE

class AdapterSensor(
    private val activity: FragmentActivity
) : ListAdapter<UISensor, AdapterSensor.ViewHolderSensor>(DIFF_CALLBACK) {

    private val navController: NavController =
        (activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSensor {
        val binding = ItemSensorBinding.inflate(
            LayoutInflater.from(activity),
            parent,
            false
        )
        return ViewHolderSensor(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderSensor, position: Int) {
        holder.binding.lifecycleOwner = activity

        getItem(position)?.let { item ->
            holder.binding.model = item

            if (item.valuesCount == 1) {
                holder.yVal.visibility = View.GONE
                holder.zVal.visibility = View.GONE
            } else {
                holder.yVal.visibility = View.VISIBLE
                holder.zVal.visibility = View.VISIBLE
            }

            when (item.sensorType) {
                Sensor.TYPE_ACCELEROMETER -> bindAccelerometer(item, holder)
                Sensor.TYPE_MAGNETIC_FIELD -> bindMagnetometer(item, holder)
                Sensor.TYPE_GRAVITY -> bindGravity(item, holder)
                Sensor.TYPE_GYROSCOPE -> bindGyroscope(item, holder)
                Sensor.TYPE_LINEAR_ACCELERATION -> bindLinearAcceleration(item, holder)
                Sensor.TYPE_AMBIENT_TEMPERATURE -> bindTemperature(item, holder)
                Sensor.TYPE_LIGHT -> bindLight(item, holder)
                Sensor.TYPE_PRESSURE -> bindPressure(item, holder)
                Sensor.TYPE_RELATIVE_HUMIDITY -> bindHumidity(item, holder)
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> bindRotationVector(item, holder)
                Sensor.TYPE_PROXIMITY -> bindProximity(item, holder)
                Sensor.TYPE_STEP_COUNTER -> bindStepCounter(item, holder)
            }
        }
    }

    private fun bindAccelerometer(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_acceleration),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.red)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_ACCELEROMETER)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindMagnetometer(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_magnet),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.pink)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_MAGNETIC_FIELD)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindGravity(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_gravity),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.purple)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GRAVITY)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindGyroscope(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_gyroscope),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.deep_blue)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GYROSCOPE)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindLinearAcceleration(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_linearacceleration),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.indigo)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_LINEAR_ACCELERATION)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindTemperature(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_temperature),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.blue)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_AMBIENT_TEMPERATURE)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindLight(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_light),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.light_blue)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_LIGHT)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindPressure(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_pressure),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.cyan)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_PRESSURE)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindHumidity(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_humidity),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.teal)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_RELATIVE_HUMIDITY)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindRotationVector(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_rotate),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.green)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindProximity(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_proximity),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.light_green)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_PROXIMITY)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindStepCounter(item: UISensor, holder: ViewHolderSensor) {
        holder.initViews(
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_steps),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, R.color.lime)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_STEP_COUNTER)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    inner class ViewHolderSensor(val binding: ItemSensorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val icon: ImageView = binding.imageViewSensor
        private val title: TextView = binding.textSensorTitle
        val xVal: TextView = binding.textSensorVal1
        val yVal: TextView = binding.textSensorVal2
        val zVal: TextView = binding.textSensorVal3

        fun initViews(drawable: Drawable?, title: String, backgroundColor: Int) {
            icon.setImageDrawable(drawable)
            this.title.text = title
            binding.backgroundColorSensor.setBackgroundColor(backgroundColor)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UISensor>() {
            override fun areItemsTheSame(oldItem: UISensor, newItem: UISensor): Boolean {
                return oldItem.sensorType == newItem.sensorType
            }

            override fun areContentsTheSame(oldItem: UISensor, newItem: UISensor): Boolean {
                return false
            }
        }
    }
}
