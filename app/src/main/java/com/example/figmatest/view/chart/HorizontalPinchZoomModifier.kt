package com.example.figmatest.view.chart

import android.graphics.PointF;
import android.view.View;

import com.scichart.charting.XyDirection;
import com.scichart.charting.modifiers.PinchZoomModifier;
import com.scichart.charting.visuals.ISciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;

class HorizontalPinchZoomModifier : PinchZoomModifier() {

    override fun performZoom(point: PointF, xValue: Double, yValue: Double) {
        if (xyDirection == XyDirection.XDirection || xyDirection == XyDirection.XyDirection) {
            performZoomBy(scaleFactor * xValue, point, xAxes)
        }
    }

    private fun performZoomBy(fraction: Double, point: PointF, axes: Collection<IAxis>) {
        for (axis in axes) {
            growBy(point, axis, fraction)
        }
    }

    private fun growBy(point: PointF, axis: IAxis, fraction: Double) {
        val size = getAxisDimension(axis)
        val coord = if (axis.isHorizontalAxis()) point.x else point.y
        val maxFraction = (1.0f - coord / size.toFloat()) * fraction

        axis.zoomBy(0.0, maxFraction)
    }

    /**
     * @see PinchZoomModifier
     */
    private fun getAxisDimension(axis: IAxis): Int {
        var size = if (axis.isHorizontalAxis()) axis.layoutWidth else axis.layoutHeight
        if (axis.visibility == View.GONE) {
            val parentSurface = parentSurface
            if (parentSurface != null) {
                size = if (axis.isHorizontalAxis()) parentSurface.width else parentSurface.height
            }
        }

        return size
    }
}

