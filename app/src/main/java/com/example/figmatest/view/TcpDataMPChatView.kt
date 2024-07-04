package com.example.figmatest.view

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.activity.ComponentActivity
import com.example.figmatest.R
import com.example.figmatest.controller.TcpDataController
import com.example.figmatest.enums.EngineCommand
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class TcpDataMPChatView : ComponentActivity(), DataViewIfc {

    private lateinit var lineChart: LineChart
    private lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData
    private val chartWindow = 10 * 1000L // Window of 10 seconds in milliseconds
    private var startTime: Long = 0
    private var firstDataReceivedTime: Long = 0
    private var entries: ArrayList<Entry> = ArrayList()
    private val gapSize = 1000L // Clear data points 1 second ahead
    private lateinit var tcpDataController: TcpDataController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tcp_data_view);
        lineChart = findViewById(R.id.chart)

        tcpDataController = TcpDataController(this, this)
        tcpDataController.start()

        setupChart()

        // Initialize start time
        startTime = SystemClock.elapsedRealtime()
    }

    fun onSendSettingsPressed(v: View?) {
        tcpDataController.onSendSettingsPressed()
    }

    fun onSendCommandPressed(v: View?) {
        tcpDataController.onSendCommandPressed(EngineCommand.START)
    }

    private fun setupChart() {
        lineDataSet = LineDataSet(entries, "Tidal Volume")
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(false)

        lineData = LineData(lineDataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(5, true)
        xAxis.granularity = 1000f // 1 second in milliseconds
        xAxis.valueFormatter = XAxisValueFormatter()

        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(false)

        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false

        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.xAxis.axisMinimum = 0f
        lineChart.xAxis.axisMaximum = chartWindow.toFloat()
    }

    override fun displayData(data: Float) {
        val currentTime = System.currentTimeMillis()

        if (firstDataReceivedTime == 0L) {
            startTime = currentTime
            firstDataReceivedTime = currentTime
        }

        var curX = (currentTime - startTime).toFloat()
        // Add an empty entry to create a gap ahead of the current dat
        if (currentTime - startTime - chartWindow > 0) {
            curX = (currentTime - startTime - chartWindow).toFloat()
            startTime += chartWindow
            //entries.clear()
            entries.removeIf { entry -> ((entry.x >= 0.0f) && (entry.x < curX + 1000))}
            //entries.add(Entry(0.0f, Float.NaN))
        } else {
            entries.removeIf { entry -> ((entry.x >= curX) && (entry.x < curX + 1000))}
        }


        entries.add(Entry(curX, data))
        lineDataSet.notifyDataSetChanged()
        lineData.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.setVisibleXRangeMaximum(chartWindow.toFloat())
        //lineChart.moveViewToX((currentTime - startTime).toFloat() - 100)
        lineChart.invalidate()
    }

    inner class XAxisValueFormatter : IndexAxisValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val seconds = value / 1000
            val milliseconds = value % 1000
            return String.format("%.0fs %.0fms", seconds, milliseconds)
        }
    }
}