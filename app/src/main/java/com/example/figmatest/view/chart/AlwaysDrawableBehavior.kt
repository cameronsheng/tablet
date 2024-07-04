package com.example.figmatest.view.chart

import android.content.Context
import android.graphics.Canvas;
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import com.scichart.charting.modifiers.IChartModifier
import com.scichart.charting.modifiers.behaviors.ModifierBehavior
import com.scichart.charting.visuals.layout.CanvasLayout


abstract class AlwaysDrawableBehavior<T : IChartModifier?>(modifierType: Class<T>?) :
    ModifierBehavior<T>(modifierType) {

    private var overlay: Overlay? = null

    protected abstract fun onDrawOverlay(canvas: Canvas?)
    override fun attachTo(target: IChartModifier) {
        super.attachTo(target)
        val modifierSurface = target.modifierSurface
        overlay = Overlay(target.context)
        modifierSurface.safeAdd(overlay)
    }

    override fun detach() {
        modifier!!.modifierSurface.safeRemove(overlay)
        super.detach()
    }

    override fun onBeginUpdate(point: PointF) {
        super.onBeginUpdate(point)
        tryInvalidate()
    }

    private fun tryInvalidate() {
        if (isAttached) {
            overlay?.postInvalidate()
        }
    }

    override fun onUpdate(point: PointF) {
        super.onUpdate(point)
        tryInvalidate()
    }

    override fun onEndUpdate(point: PointF) {
        super.onEndUpdate(point)
        tryInvalidate()
    }

    override fun clear() {
        super.clear()
        tryInvalidate()
    }

    private inner class Overlay(context: Context?) : View(context) {
        private val layoutParams = CanvasLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        init {
            setLayoutParams(layoutParams)
            setWillNotDraw(false)
        }

        override fun onDraw(canvas: Canvas) {
            if (getIsEnabled()) {
                onDrawOverlay(canvas)
            }
        }
    }
}

