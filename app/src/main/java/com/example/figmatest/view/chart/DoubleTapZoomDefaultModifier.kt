package com.example.figmatest.view.chart

import android.view.MotionEvent
import com.scichart.charting.modifiers.GestureModifierBase
import com.scichart.data.model.DoubleRange


class DoubleTapZoomDefaultModifier : GestureModifierBase<Any?>() {
    init {
        receiveHandledEvents = true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        super.onDoubleTap(event)
        performZoom()
        return true
    }

    protected fun performZoom() {
        val parentSurface = parentSurface ?: return
        val visibleRange =
            DoubleRange(0.0, ChartSurfaceIfc.CHART_DURATION_SECONDS_DEFAULT.toDouble())
        xAxis.animateVisibleRangeTo(visibleRange, 0)
        parentSurface.animateZoomExtentsY(DEFAULT_ANIMATION_DURATION)
    }

    companion object {
        const val DEFAULT_ANIMATION_DURATION: Long = 500
    }
}

