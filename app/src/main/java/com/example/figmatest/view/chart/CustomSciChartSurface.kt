package com.example.figmatest.view.chart

import android.content.Context
import android.util.AttributeSet
import com.scichart.charting.model.ChartModifierCollection
import com.scichart.charting.visuals.SciChartSurface


/**
 * Workaround the issue of rendering problems in design editor by creating custom SciChartSurface and set empty ChartModifiers collection.
 */
class CustomSciChartSurface : SciChartSurface {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initInEditMode()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initInEditMode()
    }

    private fun initInEditMode() {
        if (isInEditMode) {
            chartModifiers = ChartModifierCollection()
        }
    }
}

