package com.example.figmatest.view.chart

import com.scichart.charting.model.dataSeries.XyDataSeries


interface ChartSurfaceIfc {
    /**
     * Add values to chart surface.
     */
    fun addValues(samples: DoubleArray?, size: Int)

    /**
     * Add value to chart surface.
     */
    fun addSingleValue(sample: Double)

    /**
     * Clear all data and reset visible duration to default.
     */
    fun reset()

    /**
     * Enable/disable the Y-axis line cursor.
     *
     * @param enabled true - cursor enable; false - cursor disabled.
     */
    fun enableVerticalSliceCursor(enabled: Boolean)

    /**
     * Allows the user to freely zoom & pan on the chart surface in all directions.
     *
     * @param enabled true - zoom & pan for X-axis and Y-axis; false - zoom & pan only for X-axis, Y-axis is autoscale.
     */
    fun enableFreeZooming(enabled: Boolean)

    /**
     * Get the data which is currently displayed.
     *
     * @return raw data on which the chart operates on
     */
    val dataSeries: XyDataSeries<Double, Double>?

    /**
     * Get the position of the position cursor (=position until data of getChartDataSeries is valid).
     *
     * @return current position until the x-axis has valid data
     */
    val xAxisPositionCounter: Int

    companion object {
        const val CHART_DURATION_SECONDS_MAX = 60
        const val CHART_DURATION_SECONDS_DEFAULT = 10
        const val SAMPLING_RATE_HZ_DEFAULT = 1/0.06
    }
}