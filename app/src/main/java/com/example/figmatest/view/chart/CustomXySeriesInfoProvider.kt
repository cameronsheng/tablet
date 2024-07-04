package com.example.figmatest.view.chart

import android.R
import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider
import com.scichart.charting.visuals.renderableSeries.hitTest.XySeriesInfo
import com.scichart.charting.visuals.renderableSeries.tooltips.ISeriesTooltip


class CustomXySeriesInfoProvider : DefaultXySeriesInfoProvider() {
    override fun getSeriesTooltipInternal(
        context: Context,
        seriesInfo: XySeriesInfo<*, *, *>?
    ): ISeriesTooltip {
        val tooltip = super.getSeriesTooltipInternal(context, seriesInfo)
        if (tooltip is TextView) {
            val textView = tooltip as TextView
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                16F
            )
        }
        return tooltip
    }
}

