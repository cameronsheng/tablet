package com.example.figmatest.view.chart

import android.graphics.Paint
import android.graphics.PointF
import com.scichart.charting.modifiers.TooltipModifierWithAxisLabelsBase
import com.scichart.charting.modifiers.behaviors.AxisTooltipsBehavior
import com.scichart.charting.modifiers.behaviors.ModifierBehavior
import com.scichart.charting.modifiers.behaviors.RolloverBehavior
import com.scichart.charting.themes.IThemeProvider
import com.scichart.charting.visuals.ISciChartSurface
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.core.observable.ObservableCollection


class VerticalSliceModifier
/**
 * Creates a new instance of the [VerticalSliceModifier] class.
 */
    :
    TooltipModifierWithAxisLabelsBase(
        RolloverBehavior<VerticalSliceModifier>(VerticalSliceModifier::class.java),
        AxisTooltipsBehavior<VerticalSliceModifier>(
            VerticalSliceModifier::class.java
        )
    ) {
    private val verticalLinePaint: Paint = Paint()
    private var drawVerticalLine = true
    private val lineBehavior: AlwaysDrawableBehavior<*> = VerticalSliceAlwaysDrawableBehavior(
        VerticalSliceModifier::class.java
    )

    /**
     * Gets the [Paint] instance which will be used by [VerticalSliceAlwaysDrawableBehavior] to draw the vertical line.
     *
     * @return The [Paint] instance which will be used by [VerticalSliceAlwaysDrawableBehavior] to draw the vertical line.
     */
    fun getVerticalLinePaint(): Paint {
        return verticalLinePaint
    }

    /**
     * Gets whether a Vertical Line should be drawn at the rollover location.
     *
     * @return True if a Vertical Line should be drawn, else false.
     */
    fun getDrawVerticalLine(): Boolean {
        return drawVerticalLine
    }

    /**
     * Sets whether a Vertical Line should be drawn at the rollover location.
     */
    fun setDrawVerticalLine(drawVerticalLine: Boolean) {
        if (this.drawVerticalLine == drawVerticalLine) {
            return
        }
        this.drawVerticalLine = drawVerticalLine
    }

    /**
     * {@inheritDoc}.
     */
    override fun applyThemeProvider(themeProvider: IThemeProvider) {
        super.applyThemeProvider(themeProvider)
        themeProvider.rolloverLineStyle.initPaint(verticalLinePaint)
    }

    /**
     * {@inheritDoc}.
     */
    override fun onIsEnabledChanged(isEnabled: Boolean) {
        super.onIsEnabledChanged(isEnabled)
        if (isEnabled) {
            // position slice by default in the middle of the chart
            val point = PointF(parentSurface.width / 2.0f, parentSurface.height / 2.0f)
            super.handleMasterTouchDownEvent(point)
            lineBehavior.onBeginUpdate(point)
        } else {
            // remove and reset slice
            clearAll()
        }
    }

    /**
     * {@inheritDoc}.
     */
    override fun attachTo(target: ISciChartSurface) {
        super.attachTo(target)
        ModifierBehavior.attachTo(lineBehavior, this, drawVerticalLine)
    }

    /**
     * {@inheritDoc}.
     */
    override fun detach() {
        lineBehavior.detach()
        super.detach()
    }

    /**
     * {@inheritDoc}.
     */
    override fun getYAxesWithOverlays(): ObservableCollection<IAxis>? {
        return null
    }

    /**
     * {@inheritDoc}.
     */
    override fun handleMasterTouchDownEvent(point: PointF) {
        super.handleMasterTouchDownEvent(point)
        lineBehavior.onBeginUpdate(point)
    }

    /**
     * {@inheritDoc}.
     */
    override fun handleMasterTouchMoveEvent(point: PointF) {
        super.handleMasterTouchMoveEvent(point)
        lineBehavior.onUpdate(point)
    }

    /**
     * {@inheritDoc}.
     */
    override fun handleMasterTouchUpEvent(point: PointF) {
        // intentionally not calling super class (else the tooltip gets cleared)
    }

    /**
     * {@inheritDoc}.
     */
    override fun handleSlaveTouchDownEvent(point: PointF) {
        super.handleSlaveTouchDownEvent(point)
        lineBehavior.onBeginUpdate(point)
    }

    /**
     * {@inheritDoc}.
     */
    override fun handleSlaveTouchMoveEvent(point: PointF) {
        super.handleSlaveTouchMoveEvent(point)
        lineBehavior.onUpdate(point)
    }

    /**
     * {@inheritDoc}.
     */
    override fun handleSlaveTouchUpEvent(point: PointF) {
        // intentionally not calling super class (else the tooltip gets cleared)
    }

    /**
     * {@inheritDoc}.
     */
    override fun clearAll() {
        super.clearAll()
        lineBehavior.clear()
    }
}

