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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.sensor.R
import com.graytsar.sensor.databinding.ItemSensorBinding
import com.graytsar.sensor.model.ModelSensor
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE

class AdapterSensor(private val activity: FragmentActivity): ListAdapter<ModelSensor, ViewHolderSensor>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSensor {
        val binding = DataBindingUtil.inflate<ItemSensorBinding>(LayoutInflater.from(activity), R.layout.item_sensor, parent, false)
        return ViewHolderSensor(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ViewHolderSensor, position: Int) {
        holder.binding.lifecycleOwner = activity


        val f = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = f.navController //for fragment switch

        getItem(position)?.let { model ->
            holder.binding.model = model

            if(model.sensorValuesCount == 1){
                holder.tView2.visibility = View.GONE
                holder.tView3.visibility = View.GONE
            } else {
                holder.tView2.visibility = View.VISIBLE
                holder.tView3.visibility = View.VISIBLE
            }

            when (model.sensorType) {
                Sensor.TYPE_ACCELEROMETER -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_acceleration), model.title, ContextCompat.getColor(activity, R.color.colorRed))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_ACCELEROMETER)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_magnet), model.title, ContextCompat.getColor(activity, R.color.colorPink))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_MAGNETIC_FIELD)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_GRAVITY -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_gravity), model.title, ContextCompat.getColor(activity, R.color.colorPurple))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GRAVITY)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_GYROSCOPE -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_gyroscope), model.title, ContextCompat.getColor(activity, R.color.colorDeepPurple))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GYROSCOPE)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_linearacceleration), model.title, ContextCompat.getColor(activity, R.color.colorIndigo))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_LINEAR_ACCELERATION)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_temperature), model.title, ContextCompat.getColor(activity, R.color.colorBlue))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_AMBIENT_TEMPERATURE)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_LIGHT -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_light), model.title, ContextCompat.getColor(activity, R.color.colorLightBlue))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_LIGHT)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_PRESSURE -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_pressure), model.title, ContextCompat.getColor(activity, R.color.colorCyan))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_PRESSURE)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_RELATIVE_HUMIDITY -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_humidity), model.title, ContextCompat.getColor(activity, R.color.colorTeal))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_RELATIVE_HUMIDITY)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_rotate), model.title, ContextCompat.getColor(activity, R.color.colorGreen))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_PROXIMITY -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_proximity), model.title, ContextCompat.getColor(activity, R.color.colorLightGreen))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_PROXIMITY)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    holder.initViews(ContextCompat.getDrawable(activity, R.drawable.ic_steps), model.title, ContextCompat.getColor(activity, R.color.colorLime))

                    holder.binding.cardSensor.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_STEP_COUNTER)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    }
                }
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ModelSensor>(){
            override fun areItemsTheSame(oldItem: ModelSensor, newItem: ModelSensor): Boolean {
                return oldItem.sensorType == newItem.sensorType
            }

            override fun areContentsTheSame(oldItem: ModelSensor, newItem: ModelSensor): Boolean {
                return false
            }

        }
    }
}

class ViewHolderSensor(view: View, val binding:ItemSensorBinding): RecyclerView.ViewHolder(view) {
    private val icon: ImageView = binding.imageViewSensor
    private val titleView: TextView = binding.textSensorTitle
    val tView1: TextView = binding.textSensorVal1
    val tView2: TextView = binding.textSensorVal2
    val tView3: TextView = binding.textSensorVal3

    fun initViews(drawable: Drawable?, title: String, backgroundColor: Int) {
        icon.setImageDrawable(drawable)
        titleView.text = title
        binding.backgroundColorSensor.setBackgroundColor(backgroundColor)
    }
}
