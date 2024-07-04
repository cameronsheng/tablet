package com.example.figmatest.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.figmatest.R
import com.example.figmatest.controller.TcpDataController
import com.example.figmatest.enums.EngineCommand
import com.example.figmatest.view.chart.ChartSurfaceIfc
import com.example.figmatest.view.chart.CustomSciChartSurface
import com.example.figmatest.view.chart.SweepingChartSurface
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.IRange


class TcpDataView : ComponentActivity(), DataViewIfc {

    private lateinit var chart: CustomSciChartSurface
    private lateinit var tcpDataController: TcpDataController
    private lateinit var sweepingChartSurface: SweepingChartSurface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tcp_data_view);
        chart = findViewById(R.id.chart)

        tcpDataController = TcpDataController(this, this)
        tcpDataController.start()

        setupChart()

    }

    fun onSendSettingsPressed(v: View?) {
        tcpDataController.onSendSettingsPressed()
    }

    fun onSendCommandPressed(v: View?) {
        showCommandDialog()
    }

    private fun setupChart() {
        val xRange: IRange<*> =
            DoubleRange(0.0, ChartSurfaceIfc.CHART_DURATION_SECONDS_DEFAULT.toDouble())
        sweepingChartSurface = SweepingChartSurface(chart, this, "Time", "Value", xRange, R.color.purple_500, R.color.teal_700)
    }

    override fun displayData(data: Float) {
        sweepingChartSurface.addSingleValue(data.toDouble())
    }

    inner class XAxisValueFormatter : IndexAxisValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val seconds = value / 1000
            val milliseconds = value % 1000
            return String.format("%.0fs %.0fms", seconds, milliseconds)
        }
    }

    private fun showCommandDialog() {
        val options = EngineCommand.values().map { it.name.replace("_", " ") }.toTypedArray()
        var selectedOption: EngineCommand? = null

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an Option")
            .setSingleChoiceItems(options, -1) { _, which ->
                selectedOption = EngineCommand.values()[which]
            }
            .setPositiveButton("Send") { _, _ ->
                selectedOption?.let { onOptionSelected(it) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onOptionSelected(command: EngineCommand) {
        // Handle the selected option here
        Toast.makeText(this, "Selected option: $command", Toast.LENGTH_LONG).show()
        // Implement the callback logic here
        tcpDataController.onSendCommandPressed(command)
    }
}