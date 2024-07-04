package com.example.figmatest.view.chart

import com.scichart.charting.modifiers.IChartModifier
import android.graphics.Canvas;
import com.scichart.charting.visuals.axes.IAxis;

class VerticalSliceAlwaysDrawableBehavior<T : VerticalSliceModifier?>
/**
 * Creates a new instance of the [com.scichart.charting.modifiers.behaviors.RolloverVerticalLineDrawableBehavior]
 *
 * @param modifierType The type of the associated [IChartModifier]
 */
    (modifierType: Class<T>?) : AlwaysDrawableBehavior<T>(modifierType) {
    /**
     * {@inheritDoc}.
     */
    override fun onDrawOverlay(canvas: Canvas?) {
        if (!isLastPointValid) {
            return
        }
        val xAxis = modifier!!.xAxis
        val isVerticalChart = xAxis != null && xAxis.isHorizontalAxis
        if (modifier!!.getDrawVerticalLine()) {
            if (isVerticalChart) {
                if (canvas != null) {
                    canvas.drawLine(
                        lastUpdatePoint.x,
                        0F,
                        lastUpdatePoint.x,
                        canvas.getHeight().toFloat(),
                        modifier!!.getVerticalLinePaint()
                    )
                }
            } else {
                if (canvas != null) {
                    canvas.drawLine(
                        0F,
                        lastUpdatePoint.y,
                        canvas.getWidth().toFloat(),
                        lastUpdatePoint.y,
                        modifier!!.getVerticalLinePaint()
                    )
                }
            }
        }
    }
}

