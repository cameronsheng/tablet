package com.example.figmatest.view.chart

import android.view.MotionEvent

import com.scichart.charting.modifiers.ZoomPanModifier


internal class TwoFingerPanModifier : ZoomPanModifier() {
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        xDelta: Float,
        yDelta: Float
    ): Boolean {
        return e2.pointerCount == 2 && super.onScroll(e1, e2, xDelta, yDelta)
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return e2.pointerCount == 2 && super.onFling(e1, e2, velocityX, velocityY)
    }
}

