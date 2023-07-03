package com.graytsar.sensor.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.graytsar.sensor.R
import com.graytsar.sensor.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentDetail : Fragment() {
    private val viewModel: ViewModelDetail by viewModels()

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var fab: FloatingActionButton
    private lateinit var name: TextView
    private lateinit var vendor: TextView
    private lateinit var version: TextView
    private lateinit var power: TextView
    private lateinit var maxDelay: TextView
    private lateinit var minDelay: TextView
    private lateinit var maxRange: TextView
    private lateinit var info: TextView


    private lateinit var mpChart: LineChart
    private var mData: LineData = LineData()

    private var sensorEventListener: SensorEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val toolbar = binding.includeToolbarDetail.toolbarDetail
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val navController = NavHostFragment.findNavController(this)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            navController
        )

        fab = binding.includeToolbarDetail.fab
        name = binding.includeToolbarDetail.name
        vendor = binding.includeToolbarDetail.vendor
        version = binding.includeToolbarDetail.version
        power = binding.includeToolbarDetail.power
        maxDelay = binding.includeToolbarDetail.maxDelay
        minDelay = binding.includeToolbarDetail.minDelay
        maxRange = binding.includeToolbarDetail.maxRange
        info = binding.includeToolbarDetail.textInformation

        initDisplayPoints()
        initSensorData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name.text = getString(R.string.labelName, viewModel.sensor.name)
        vendor.text = getString(R.string.labelVendor, viewModel.sensor.vendor)
        version.text = getString(R.string.labelVersion, viewModel.sensor.version.toString())
        power.text = getString(R.string.labelPower, viewModel.sensor.power.toString())
        maxDelay.text = getString(R.string.labelMaxDelay, viewModel.sensor.maxDelay.toString())
        minDelay.text = getString(R.string.labelMinDelay, viewModel.sensor.minDelay.toString())
        maxRange.text = getString(
            R.string.labelMaxRange,
            getString(viewModel.itemSensor.unit, viewModel.sensor.maximumRange)
        )
        info.text = getString(viewModel.itemSensor.info)

        viewModel.itemSensor.let {
            val color = ContextCompat.getColor(requireContext(), it.color)
            val toolbar: Toolbar = binding.includeToolbarDetail.toolbarDetail
            val layout = binding.includeToolbarDetail.toolbarLayout
            val icon = binding.includeToolbarDetail.appBarImage

            toolbar.title = getString(it.title)
            layout.setBackgroundColor(color)
            layout.setContentScrimColor(color)
            activity?.window?.statusBarColor = color
            icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), it.icon))
        }

        if (viewModel.enableLog) {
            fab.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            fab.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }

        fab.setOnClickListener { onFabClicked() }
    }

    override fun onStart() {
        super.onStart()
        viewModel.sensorManager.registerListener(
            sensorEventListener,
            viewModel.sensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    override fun onStop() {
        viewModel.sensorManager.unregisterListener(sensorEventListener)
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(request: Int, result: Int, resultData: Intent?) {
        if (resultData != null && resultData.data != null) {
            val fUri = resultData.data!!

            val intent = Intent(context, ForegroundServiceLogging::class.java).apply {
                putExtra("enableLog", true)
                putExtra("sensorType", viewModel.sensorType)
                putExtra("title", viewModel.itemSensor.title)
                putExtra("sensorValuesCount", viewModel.itemSensor.valuesCount)
                putExtra("csvHeader", viewModel.csvHeader)
                putExtra("fUri", fUri.toString())
            }
            ContextCompat.startForegroundService(requireContext(), intent)
        }
    }

    private fun initDisplayPoints() {
        val p = DisplayMetrics()
        val widthPixels = if (Build.VERSION.SDK_INT < 30) {
            (context as Activity).windowManager.defaultDisplay.getMetrics(p)
            p.widthPixels;
        } else {
            val windowManager: WindowManager =
                requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager;
            val metrics = windowManager.currentWindowMetrics
            val windowInsets = metrics.windowInsets
            val insets: Insets =
                windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout())
            val insetsWidth: Int = insets.right + insets.left
            val bounds: Rect = metrics.bounds
            bounds.width() - insetsWidth
        }

        viewModel.displayPoints = widthPixels / 2.5f
        if (viewModel.displayPoints < 400)
            viewModel.displayPoints = 400f
    }

    private fun onFabClicked() {
        viewModel.enableLog = !viewModel.enableLog
        if (viewModel.enableLog) {
            fab.setImageResource(R.drawable.ic_baseline_pause_24)
            Snackbar.make(binding.root, getString(R.string.startRecording), Snackbar.LENGTH_LONG)
                .show()

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TITLE, viewModel.itemSensor.title.toString() + ".txt")
            startActivityForResult(intent, 1)
        } else if (!viewModel.enableLog) {
            fab.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            val intent = Intent(context, ForegroundServiceLogging::class.java)
            intent.putExtra("enableLog", false)
            ContextCompat.startForegroundService(requireContext(), intent)
        }
    }

    private fun initSensorData() {
        mpChart = binding.includeToolbarDetail.chart
        setupChart(viewModel.itemSensor.valuesCount, mData, mpChart)

        if (viewModel.itemSensor.valuesCount == 1) {
            sensorEventListener = object : SensorEventListener {
                var i = 0
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return
                    viewModel.xValue.postValue(
                        getString(
                            viewModel.itemSensor.unit,
                            event.values[0]
                        )
                    )
                    addPointsToChart(i, event.values)
                    i++
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /* do nothing */
                }
            }
        } else {
            sensorEventListener = object : SensorEventListener {
                var i = 0
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return
                    viewModel.xValue.postValue(
                        getString(
                            viewModel.itemSensor.unit,
                            event.values[0]
                        )
                    )
                    viewModel.yValue.postValue(
                        getString(
                            viewModel.itemSensor.unit,
                            event.values[1]
                        )
                    )
                    viewModel.zValue.postValue(
                        getString(
                            viewModel.itemSensor.unit,
                            event.values[2]
                        )
                    )
                    addPointsToChart(i, event.values)
                    i++
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /* do nothing */
                }
            }
        }
    }

    private fun addPointsToChart(position: Int, valueAr: FloatArray) {
        for ((i, c) in (0 until viewModel.itemSensor.valuesCount).withIndex()) {
            val entry = Entry(position.toFloat(), valueAr[i])
            mData.addEntry(entry, i)
            if (mData.getDataSetByIndex(i).entryCount > viewModel.displayPoints)
                mData.getDataSetByIndex(i).removeFirst()
        }

        mData.notifyDataChanged() //tell LineData to do its magic
        mpChart.notifyDataSetChanged() //tell LineChart to do its magic
        mpChart.setVisibleXRange(viewModel.displayPoints, viewModel.displayPoints)
        mpChart.moveViewToX(position.toFloat()) //auto calls invalidate

    }

    private fun setupChart(countGraphs: Int, mData: LineData, mChart: LineChart) {
        val colorAr: IntArray = intArrayOf(Color.BLUE, Color.RED, Color.GREEN)

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

        for (i in 0 until countGraphs) {
            mData.addDataSet(createSet(('x'.code + i).toChar().toString(), colorAr[i]))
        }
        mChart.data = mData
    }

    private fun createSet(description: String, _color: Int): LineDataSet {
        val set = LineDataSet(null, description)
        set.setDrawCircles(false)//disable circles around points
        set.color = _color
        set.lineWidth = 2f
        set.setDrawValues(false) //disabled point labels
        return set
    }
}