package com.graytsar.sensor.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.graytsar.sensor.ForegroundServiceLogging
import com.graytsar.sensor.MainActivity
import com.graytsar.sensor.R
import com.graytsar.sensor.databinding.FragmentDetailBinding
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE

class FragmentDetail : Fragment() {
    private val viewModelDetail:ViewModelDetail by viewModels<ViewModelDetail>()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var fab:FloatingActionButton
    private lateinit var sensorManager: SensorManager

    private lateinit var mpChart:LineChart
    private var mData: LineData = LineData()

    private var sensorType:Int = -1
    private var sensorEventListener:SensorEventListener? = null

    private var displayPoints:Float = 400f

    private var csvHeader:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            sensorType = bundle.getInt(ARG_SENSOR_TYPE, -1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)


        val toolbar: Toolbar = binding.includeToolbarDetail.toolbarDetail
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)

        val navController = NavHostFragment.findNavController(this)
        NavigationUI.setupActionBarWithNavController(this.context as MainActivity, navController)


        binding.includeToolbarDetail.lifecycleOwner = this
        binding.includeToolbarDetail.viewModel = viewModelDetail


        fab = binding.includeToolbarDetail.floatingActionButtonDetail
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager


        initSensorData()


        val p = DisplayMetrics()
        if(Build.VERSION.SDK_INT < 30) {
            (context as Activity).windowManager.defaultDisplay.getMetrics(p)
        } else {
            requireActivity().display?.getRealMetrics(p)
        }
        displayPoints = p.widthPixels / 2.5f

        if(displayPoints < 400)
            displayPoints = 400f


        if(viewModelDetail.enableLog) {
            fab.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            fab.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }

        fab.setOnClickListener {
            viewModelDetail.enableLog = !viewModelDetail.enableLog
            if(viewModelDetail.enableLog){
                fab.setImageResource(R.drawable.ic_baseline_pause_24)
                Snackbar.make(binding.root, getString(R.string.startRecording), Snackbar.LENGTH_LONG).show()

                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TITLE, viewModelDetail.name + ".txt")
                startActivityForResult(intent, 1)
            } else if(!viewModelDetail.enableLog){
                fab.setImageResource(R.drawable.ic_baseline_play_arrow_24)

                val intent = Intent(context, ForegroundServiceLogging::class.java)
                intent.putExtra("enableLog", false)
                ContextCompat.startForegroundService(requireContext(), intent)
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onStop() {
        super.onStop()

        sensorManager.unregisterListener(sensorEventListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModelDetail.enableLog = false
    }

    override fun onActivityResult(request:Int, result:Int, resultData: Intent?){
        if(resultData != null && resultData.data != null){
            val fUri = resultData.data!!

            val intent = Intent(context, ForegroundServiceLogging::class.java).apply {
                putExtra("enableLog", true)
                putExtra("sensorType", sensorType)
                putExtra("title", viewModelDetail.name)
                putExtra("sensorValuesCount", viewModelDetail.count)
                putExtra("csvHeader", csvHeader)
                putExtra("fUri", fUri.toString())
            }
            ContextCompat.startForegroundService(requireContext(), intent)
        }
    }

    private fun initSensorData() {
        if(sensorType > -1) {
            val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
            val toolbar: Toolbar = binding.includeToolbarDetail.toolbarDetail
            val toolbarLayout = binding.includeToolbarDetail.toolbarLayout
            val icon = binding.includeToolbarDetail.appBarImage

            when (sensorType) {
                Sensor.TYPE_ACCELEROMETER -> {
                    viewModelDetail.name = getString(R.string.sensorAccelerometer)
                    viewModelDetail.count = 3
                    viewModelDetail.unit = getString(R.string.unitAcceleration)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoAccelerometer)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorRed)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_acceleration))
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    viewModelDetail.name = getString(R.string.sensorMagneticField)
                    viewModelDetail.count = 3
                    viewModelDetail.unit = getString(R.string.unitMagneticField)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoMagneticField)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPink))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPink)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorPink))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_magnet))
                }
                Sensor.TYPE_GRAVITY -> {
                    viewModelDetail.name = getString(R.string.sensorGravity)
                    viewModelDetail.count = 3
                    viewModelDetail.unit = getString(R.string.unitAcceleration)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoGravity)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPurple))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPurple)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorPurple))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_gravity))
                }
                Sensor.TYPE_GYROSCOPE -> {
                    viewModelDetail.name = getString(R.string.sensorGyroscope)
                    viewModelDetail.count = 3
                    viewModelDetail.unit = getString(R.string.unitRadiantSecond)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoGyroscope)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorDeepPurple))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorDeepPurple)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorDeepPurple))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_gyroscope))
                }
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    viewModelDetail.name = getString(R.string.sensorLinearAcceleration)
                    viewModelDetail.count = 3
                    viewModelDetail.unit = getString(R.string.unitAcceleration)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoLinearAcceleration)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorIndigo))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorIndigo)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorIndigo))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_linearacceleration))
                }
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    viewModelDetail.name = getString(R.string.sensorAmbientTemperature)
                    viewModelDetail.count = 1
                    viewModelDetail.unit = getString(R.string.unitTemperature)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoAmbientTemperature)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBlue))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorBlue)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorBlue))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_temperature))
                }
                Sensor.TYPE_LIGHT -> {
                    viewModelDetail.name = getString(R.string.sensorLight)
                    viewModelDetail.count = 1
                    viewModelDetail.unit = getString(R.string.unitLight)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoLight)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorLightBlue))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorLightBlue)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorLightBlue))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_light))
                }
                Sensor.TYPE_PRESSURE -> {
                    viewModelDetail.name = getString(R.string.sensorPressure)
                    viewModelDetail.count = 1
                    viewModelDetail.unit = getString(R.string.unitPressure)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoPressure)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorCyan))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorCyan)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorCyan))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pressure))
                }
                Sensor.TYPE_RELATIVE_HUMIDITY -> {
                    viewModelDetail.name = getString(R.string.sensorRelativeHumidity)
                    viewModelDetail.count = 1
                    viewModelDetail.unit = getString(R.string.unitPercent)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoRelativeHumidity)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorTeal))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorTeal)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorTeal))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_humidity))
                }
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                    viewModelDetail.name = getString(R.string.sensorGeomagneticRotationVector)
                    viewModelDetail.count = 3
                    viewModelDetail.unit = ""
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoGeomagneticRotationVector)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorGreen)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_rotate))
                }
                Sensor.TYPE_PROXIMITY -> {
                    viewModelDetail.name = getString(R.string.sensorProximity)
                    viewModelDetail.count = 1
                    viewModelDetail.unit = getString(R.string.unitProximity)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoProximity)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorLightGreen))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorLightGreen)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorLightGreen))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_proximity))
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    viewModelDetail.name = getString(R.string.sensorStepCounter)
                    viewModelDetail.count = 1
                    viewModelDetail.unit = getString(R.string.unitSteps)
                    binding.includeToolbarDetail.sensorTextInformation.text = getString(R.string.infoStepCounter)
                    toolbar.title = viewModelDetail.name
                    toolbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorLime))
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorLime)
                    toolbarLayout.setContentScrimColor(ContextCompat.getColor(requireContext(), R.color.colorLime))
                    icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_steps))
                }
            }

            binding.includeToolbarDetail.sensorTextName.text = "${getString(R.string.labelName)} ${sensor.name}"
            binding.includeToolbarDetail.sensorTextVendor.text = "${getString(R.string.labelVendor)} ${sensor.vendor}"
            binding.includeToolbarDetail.sensorTextVersion.text = "${getString(R.string.labelVersion)} ${sensor.version}"
            binding.includeToolbarDetail.sensorTextPower.text = "${getString(R.string.labelPower)} ${sensor.power}"
            binding.includeToolbarDetail.sensorTextMaxDelay.text = "${getString(R.string.labelMaxDelay)} ${sensor.maxDelay}"
            binding.includeToolbarDetail.sensorTextMinDelay.text = "${getString(R.string.labelMinDelay)} ${sensor.minDelay}"
            binding.includeToolbarDetail.sensorTextMaxRange.text = "${getString(R.string.labelMaxRange)} ${sensor.maximumRange} ${viewModelDetail.unit}"


            mpChart = binding.includeToolbarDetail.chart
            setupChart(viewModelDetail.count, mData, mpChart)

            csvHeader = "TIMESTAMP,X,Y,Z,${sensor.name},VENDOR:${sensor.vendor},VERSION:${sensor.version},POWER:${sensor.power},MAXDELAY:${sensor.maxDelay},MINDELAY:${sensor.minDelay},MAXRANGE:${sensor.maximumRange}"

            if(viewModelDetail.count == 1) {
                sensorEventListener = object : SensorEventListener {
                    var i = 0
                    override fun onSensorChanged(event: SensorEvent?) {
                        event?.let {
                            viewModelDetail.xValue.postValue("${event.values[0]} ${viewModelDetail.unit}")

                            addPointsToChart(i, event.values)
                            i++
                        }
                    }

                    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                    }
                }
            } else {
                sensorEventListener = object : SensorEventListener {
                    var i = 0
                    override fun onSensorChanged(event: SensorEvent?) {
                        event?.let{
                            viewModelDetail.xValue.postValue("${event.values[0]} ${viewModelDetail.unit}")
                            viewModelDetail.yValue.postValue("${event.values[1]} ${viewModelDetail.unit}")
                            viewModelDetail.zValue.postValue("${event.values[2]} ${viewModelDetail.unit}")

                            addPointsToChart(i, event.values)
                            i++
                        }
                    }

                    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                    }

                }
            }
        }
    }

    private fun addPointsToChart(position:Int, valueAr:FloatArray){

        for((i, c) in (0 until viewModelDetail.count).withIndex()) {
            val entry = Entry(position.toFloat(), valueAr[i])
            mData.addEntry(entry, i)
            if (mData.getDataSetByIndex(i).entryCount > displayPoints)
                mData.getDataSetByIndex(i).removeFirst()
        }

        mData.notifyDataChanged() //tell LineData to do its magic
        mpChart.notifyDataSetChanged() //tell LineChart to do its magic
        mpChart.setVisibleXRange(displayPoints, displayPoints)
        mpChart.moveViewToX(position.toFloat()) //auto calls invalidate

    }

    private fun setupChart(countGraphs:Int, mData: LineData, mChart: LineChart){
        val colorAr:IntArray = intArrayOf(Color.BLUE, Color.RED, Color.GREEN)

        mChart.description.isEnabled = false //disable description at the right bottom corner
        mChart.legend.textSize = 16f
        mChart.legend.textColor = Color.GRAY
        mChart.axisLeft.isEnabled = false
        mChart.axisLeft.setDrawGridLines(false) //disable background grid lines
        mChart.axisRight.textSize = 16f
        mChart.axisRight.setDrawGridLines(false) //disable background grid lines
        mChart.axisRight.textColor = Color.GRAY
        mChart.xAxis.setDrawGridLines(false) //disable background grid lines
        mChart.xAxis.position = XAxis.XAxisPosition.BOTTOM //x axis position
        mChart.xAxis.setDrawLabels(false) //disable draw axis labels
        mChart.setTouchEnabled(false) //disable all touch related stuff
        mChart.setHardwareAccelerationEnabled(true)

        for(i in 0 until countGraphs)
        {
            mData.addDataSet(createSet(('x'.toInt() + i).toChar() + " Axis", colorAr[i]))
        }
        mChart.data = mData
    }

    private fun createSet(description:String, _color:Int): LineDataSet {
        val set = LineDataSet(null, description)
        set.setDrawCircles(false)//disable circles around points
        set.color = _color
        set.lineWidth = 2f
        set.setDrawValues(false) //disabled point labels
        return set
    }
}