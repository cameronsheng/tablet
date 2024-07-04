package com.example.figmatest.view.chart

import com.scichart.charting.numerics.tickProviders.NumericTickProvider
import com.scichart.core.model.DoubleValues


internal class CustomNumericTickProvider : NumericTickProvider() {
    override fun updateTicks(minorTicks: DoubleValues, majorTicks: DoubleValues) {
        super.updateTicks(minorTicks, majorTicks)
        // remove first tick if distance to VisibleRange.Min < MajorDelta
        val firstMajorTick = majorTicks[0]
        if (firstMajorTick < currentVisibleRangeMin + currentMajorDelta / 4) {
            majorTicks.remove(0)
        }

        // remove last tick if distance to VisibleRange.Max < MajorDelta
        val lastTickIndex = majorTicks.size() - 1
        val lastMajorTick = majorTicks[lastTickIndex]
        if (lastMajorTick > currentVisibleRangeMax - currentMajorDelta / 4) {
            majorTicks.remove(lastTickIndex)
        }
    }
}

