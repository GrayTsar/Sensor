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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.sensor.R
import com.graytsar.sensor.databinding.ItemSensorBinding
import com.graytsar.sensor.model.UISensor
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        getItem(position)?.let { item ->
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
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_ACCELEROMETER)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindMagnetometer(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_MAGNETIC_FIELD)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindGravity(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GRAVITY)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindGyroscope(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GYROSCOPE)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindLinearAcceleration(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_LINEAR_ACCELERATION)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindTemperature(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_AMBIENT_TEMPERATURE)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindLight(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_LIGHT)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindPressure(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_PRESSURE)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindHumidity(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_RELATIVE_HUMIDITY)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindRotationVector(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindProximity(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_PROXIMITY)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    private fun bindStepCounter(item: UISensor, holder: ViewHolderSensor) {
        holder.initView(
            item = item,
            drawable = ContextCompat.getDrawable(activity, item.icon),
            title = activity.getString(item.title),
            backgroundColor = ContextCompat.getColor(activity, item.color)
        )

        holder.binding.cardSensor.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, Sensor.TYPE_STEP_COUNTER)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        }
    }

    override fun onViewRecycled(holder: ViewHolderSensor) {
        super.onViewRecycled(holder)
    }

    inner class ViewHolderSensor(val binding: ItemSensorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val icon: ImageView = binding.icon
        private val title: TextView = binding.title
        private val xVal: TextView = binding.xValue
        private val yVal: TextView = binding.yValue
        private val zVal: TextView = binding.zValue

        fun initView(item: UISensor, drawable: Drawable?, title: String, backgroundColor: Int) {
            icon.setImageDrawable(drawable)
            this.title.text = title
            binding.background.setBackgroundColor(backgroundColor)

            if (item.valuesCount == 1) {
                yVal.visibility = View.GONE
                zVal.visibility = View.GONE
            } else {
                yVal.visibility = View.VISIBLE
                zVal.visibility = View.VISIBLE
            }

            activity.lifecycleScope.launch {
                if (item.valuesCount == 1) {
                    item.values.collectLatest {
                        xVal.text = activity.getString(item.unit, it.first)
                    }
                } else {
                    item.values.collectLatest {
                        xVal.text = activity.getString(item.unit, it.first)
                        yVal.text = activity.getString(item.unit, it.second)
                        zVal.text = activity.getString(item.unit, it.third)
                    }
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UISensor>() {
            override fun areItemsTheSame(oldItem: UISensor, newItem: UISensor): Boolean {
                return oldItem.sensorType == newItem.sensorType
            }

            override fun areContentsTheSame(oldItem: UISensor, newItem: UISensor): Boolean {
                return oldItem == newItem
            }
        }
    }
}
