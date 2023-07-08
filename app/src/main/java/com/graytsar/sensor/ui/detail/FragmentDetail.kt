package com.graytsar.sensor.ui.detail

import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
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
    private lateinit var xText: TextView
    private lateinit var yText: TextView
    private lateinit var zText: TextView

    private lateinit var chart: AAChartView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val toolbar = binding.detail.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val navController = NavHostFragment.findNavController(this)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            navController
        )

        fab = binding.detail.fab
        name = binding.detail.name
        vendor = binding.detail.vendor
        version = binding.detail.version
        power = binding.detail.power
        maxDelay = binding.detail.maxDelay
        minDelay = binding.detail.minDelay
        maxRange = binding.detail.maxRange
        info = binding.detail.textInformation
        chart = binding.detail.chart

        xText = binding.detail.xValDetail
        yText = binding.detail.yValDetail
        zText = binding.detail.zValDetail

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

        if (viewModel.itemSensor.axes == 3) {
            yText.visibility = View.VISIBLE
            zText.visibility = View.VISIBLE
        }

        viewModel.itemSensor.let {
            val color = ContextCompat.getColor(requireContext(), it.color)
            val layout = binding.detail.toolbarLayout
            val icon = binding.detail.appBarImage

            layout.title = getString(it.title)
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

        viewModel.singleUpdate = { updateSingleChart() }
        viewModel.multiUpdate = { updateMultiChart() }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.itemSensor.axes == 1) {
            updateSingleChart()
        } else {
            updateMultiChart()
        }

        viewModel.sensorManager.registerListener(
            viewModel.sensorEventListener,
            viewModel.sensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onStop() {
        viewModel.singleUpdate = null
        viewModel.multiUpdate = null
        viewModel.sensorManager.unregisterListener(viewModel.sensorEventListener)
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
                putExtra("sensorValuesCount", viewModel.itemSensor.axes)
                putExtra("csvHeader", viewModel.csvHeader)
                putExtra("fUri", fUri.toString())
            }
            ContextCompat.startForegroundService(requireContext(), intent)
        }
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

    private fun updateSingleChart() {
        xText.text = getString(
            viewModel.itemSensor.unit,
            viewModel.xValues.lastOrNull() ?: 0.0
        )

        val chartModel = AAChartModel(
            chartType = AAChartType.Line,
            animationDuration = 0,
            animationType = null,
            touchEventEnabled = false,
            tooltipEnabled = false,
            dataLabelsEnabled = false,
            xAxisLabelsEnabled = false,
            yAxisTitle = "",
            yAxisGridLineWidth = 0f,
            backgroundColor = "#00000000",
            axesTextColor = "#78909C"
        ).apply {
            series(
                arrayOf(
                    mapOf(
                        "name" to "X",
                        "data" to viewModel.xValues.toTypedArray()
                    )
                )
            )
        }
        chart.aa_drawChartWithChartModel(chartModel)
    }

    private fun updateMultiChart() {
        xText.text = getString(
            viewModel.itemSensor.unit,
            viewModel.xValues.lastOrNull() ?: 0.0
        )
        yText.text = getString(
            viewModel.itemSensor.unit,
            viewModel.yValues.lastOrNull() ?: 0.0
        )
        zText.text = getString(
            viewModel.itemSensor.unit,
            viewModel.zValues.lastOrNull() ?: 0.0
        )

        val chartModel = AAChartModel(
            chartType = AAChartType.Line,
            animationDuration = 0,
            animationType = null,
            touchEventEnabled = false,
            tooltipEnabled = false,
            dataLabelsEnabled = false,
            xAxisLabelsEnabled = false,
            yAxisTitle = "",
            yAxisGridLineWidth = 0f,
            backgroundColor = "#00000000",
            axesTextColor = "#78909C"
        ).apply {
            series(
                arrayOf(
                    mapOf(
                        "name" to "X",
                        "data" to viewModel.xValues.toTypedArray()
                    ),
                    mapOf(
                        "name" to "Y",
                        "data" to viewModel.yValues.toTypedArray()
                    ),
                    mapOf(
                        "name" to "Z",
                        "data" to viewModel.zValues.toTypedArray()
                    )
                )
            )
        }
        chart.aa_drawChartWithChartModel(chartModel)
    }
}