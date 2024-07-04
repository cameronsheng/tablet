package com.example.figmatest.view.chart

import android.R
import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.scichart.charting.model.ChartModifierCollection
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.IChartModifier
import com.scichart.charting.modifiers.PinchZoomModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.data.model.IRange
import com.scichart.data.model.IRangeChangeObserver
import com.scichart.drawing.common.PenStyle
import com.scichart.drawing.common.SolidBrushStyle
import java.util.Arrays
import java.util.Collections


/**
 * Wrap-around chart for signal monitoring, less CPU und GPU usage.
 * In this type of chart, a signal goes from Left to Right, then once the trace hits the right edge, it restarts at the left edge with a small gap.
 */
class SweepingChartSurface(
    private val chartSurface: SciChartSurface,
    private val context: Context?,
    xAxisTitle: String?,
    yAxisTitle: String?,
    dataSamplingRateHz: Double,
    maxChartAxisDurationSeconds: Double,
    xRange: IRange<*>,
    @ColorRes lineColorRes: Int,
    @ColorRes areaColorRes: Int
) :
    ChartSurfaceIfc {
    private var verticalSliceModifier: VerticalSliceModifier? = null
    private var horizontalPinchZoomModifier: HorizontalPinchZoomModifier? = null
    private var pinchZoomModifier: PinchZoomModifier? = null
    private var twoFingerPanModifier: TwoFingerPanModifier? = null
    private var doubleTapZoomDefaultModifier: DoubleTapZoomDefaultModifier? = null
    private var dataSamplingRateHz: Double
    val maxChartAxisDurationSeconds: Double
    private var visibleNumberOfSamples = 0
    private var maxNumberOfSamples = 0
    override var dataSeries: XyDataSeries<Double, Double>? = null
        private set
    private var xBottomAxis: NumericAxis? = null
    private var yLeftAxis: NumericAxis? = null
    private var gapSize = maxGapSize
    override var xAxisPositionCounter = 0
        private set
    private val xRange: IRange<*>

    // object recycling
    private val gapList: DoubleValues

    /**
     * Constructor to create a new instance with empty data.
     */
    constructor(
        chartSurface: SciChartSurface,
        context: Context?,
        xAxisTitle: String?,
        yAxisTitle: String?,
        xRange: IRange<*>,
        @ColorRes lineColorRes: Int,
        @ColorRes areaColorRes: Int
    ) : this(
        chartSurface,
        context,
        xAxisTitle,
        yAxisTitle,
        ChartSurfaceIfc.SAMPLING_RATE_HZ_DEFAULT,
        ChartSurfaceIfc.CHART_DURATION_SECONDS_MAX.toDouble(),
        xRange,
        lineColorRes,
        areaColorRes
    )

    init {
        xAxisPositionCounter = 0
        this.dataSamplingRateHz = dataSamplingRateHz
        this.maxChartAxisDurationSeconds = maxChartAxisDurationSeconds
        this.xRange = xRange
        gapList = DoubleValues()
        updateMaxNumberOfSamples()
        updateVisibleNumberOfSamples()
        gapSize = getGapSize()
        setupChart(xAxisTitle, yAxisTitle, lineColorRes, areaColorRes)
    }

    override fun addValues(samples: DoubleArray?, size: Int) {
        for (i in 0 until size) {
            addValue(samples!![i])
            increaseCounter()
        }
        if (getGapSize() >= MIN_GAP_SIZE) {
            addGap()
        }
        chartSurface.invalidateElement()
    }

    override fun addSingleValue(sample: Double) {
        addValue(sample)
        increaseCounter()
        if (getGapSize() >= MIN_GAP_SIZE) {
            addGap()
        }
        chartSurface.invalidateElement()
    }

    override fun reset() {
        val list: MutableList<Double> = Collections.nCopies(maxNumberOfSamples, Double.NaN)
        dataSeries?.updateRangeYAt(0, list)
        // reset to start
        visibleChartAxisDurationSeconds =
            ChartSurfaceIfc.CHART_DURATION_SECONDS_DEFAULT.toDouble()
        xAxisPositionCounter = 0
        chartSurface.invalidateElement()
    }

    override fun enableVerticalSliceCursor(enabled: Boolean) {
        verticalSliceModifier!!.isEnabled = enabled
        if (enabled) {
            // TODO: Extract vertical slice styles into styles.xml
            verticalSliceModifier!!.getVerticalLinePaint().strokeWidth = 4f
        }
    }

    override fun enableFreeZooming(enabled: Boolean) {
        if (enabled) {
            yLeftAxis!!.autoRange = AutoRange.Never
            setFreeZoomingChartModifiers()
        } else {
            setDefaultChartModifiers()
            yLeftAxis!!.autoRange = AutoRange.Always
        }
    }

    fun getDataSamplingRateHz(): Double {
        return dataSamplingRateHz
    }

    fun setDataSamplingRateHz(dataSamplingRateHz: Double) {
        this.dataSamplingRateHz = dataSamplingRateHz
        updateVisibleNumberOfSamples()
        updateMaxNumberOfSamples()
    }

    var visibleChartAxisDurationSeconds: Double
        get() = xRange.maxAsDouble
        private set(visibleChartAxisDurationSeconds) {
            var visibleChartAxisDurationSeconds = visibleChartAxisDurationSeconds
            if (visibleChartAxisDurationSeconds > maxChartAxisDurationSeconds) {
                visibleChartAxisDurationSeconds = maxChartAxisDurationSeconds
            }
            xRange.setMinMaxDouble(0.0, visibleChartAxisDurationSeconds)
            updateVisibleNumberOfSamples()
        }

    private fun setTheme(themeId: Int) {
        chartSurface.theme = themeId
    }

    fun setOnClickListener(onClickListener: View.OnClickListener?) {
        chartSurface.setOnClickListener(onClickListener)
    }

    private fun updateVisibleNumberOfSamples() {
        visibleNumberOfSamples = (xRange.maxAsDouble * dataSamplingRateHz).toInt()
    }

    private fun updateMaxNumberOfSamples() {
        maxNumberOfSamples = (maxChartAxisDurationSeconds * dataSamplingRateHz).toInt()
    }

    private fun setupChart(
        xAxisTitle: String?,
        yAxisTitle: String?,
        @ColorRes lineColorRes: Int,
        @ColorRes areaColorRes: Int
    ) {
        initDataSeries()

        // TODO: Apply xAxisTitle
        xBottomAxis = NumericAxis(context)
        xBottomAxis!!.setAxisAlignment(AxisAlignment.Bottom)
        xBottomAxis!!.setDrawMajorBands(false)
        xBottomAxis!!.setAutoRange(AutoRange.Never)
        xBottomAxis!!.setVisibleRange(xRange)
        xBottomAxis!!.setVisibleRangeLimit(DoubleRange(0.0, maxChartAxisDurationSeconds))
        xBottomAxis!!.setMinimalZoomConstrain(0.25)
        xBottomAxis!!.getVisibleRange().addRangeChangeObserver(object : IRangeChangeObserver<Double> {
            override fun onRangeChanged(
                p0: Double?,
                p1: Double?,
                p2: Double?,
                p3: Double?,
                p4: Int
            ) {
                visibleNumberOfSamples =
                    (xBottomAxis!!.getVisibleRange().maxAsDouble * dataSamplingRateHz).toInt()
                gapSize = getGapSize()
            }
        })
        xBottomAxis!!.setTickProvider(CustomNumericTickProvider())

        // TODO: Apply yAxisTitle
        yLeftAxis = NumericAxis(context)
        yLeftAxis!!.setAxisAlignment(AxisAlignment.Left)
        yLeftAxis!!.setDrawMajorBands(false)
        yLeftAxis!!.setAutoRange(AutoRange.Always)
        yLeftAxis!!.setGrowBy(DoubleRange(0.01, 0.01))

        // create FastLinesSeries
        val renderableSeries: FastMountainRenderableSeries<Double, Double> = FastMountainRenderableSeries<Double, Double>()
        renderableSeries.seriesInfoProvider = CustomXySeriesInfoProvider()
        renderableSeries.setDataSeries(dataSeries)
        renderableSeries.xAxisId = xBottomAxis!!.getAxisId()
        renderableSeries.yAxisId = yLeftAxis!!.getAxisId()

        // add series into chart
        chartSurface.xAxes.clear()
        chartSurface.yAxes.clear()
        chartSurface.renderableSeries.clear()
        Collections.addAll(chartSurface.xAxes, xBottomAxis)
        Collections.addAll(chartSurface.yAxes, yLeftAxis)
        Collections.addAll(chartSurface.renderableSeries, renderableSeries)

        // create and assign modifier collection
        createChartModifiers()
        setDefaultChartModifiers()

        // apply UI theme
        //setTheme(R.style.imtChartStyle) TODO
        renderableSeries.strokeStyle =
            PenStyle(ContextCompat.getColor(context!!, lineColorRes), true, 2f)
        renderableSeries.areaStyle =
            SolidBrushStyle(ContextCompat.getColor(context, areaColorRes))
        val majorTickLineStyle =
            PenStyle(ContextCompat.getColor(context, R.color.darker_gray), true, 1f)
        yLeftAxis!!.setMajorGridLineStyle(majorTickLineStyle)
        yLeftAxis!!.setMajorTickLineStyle(majorTickLineStyle)
        xBottomAxis!!.setMajorGridLineStyle(majorTickLineStyle)
        xBottomAxis!!.setMajorTickLineStyle(majorTickLineStyle)

        // TODO: Extract vertical slice styles into styles.xml
        verticalSliceModifier!!.getVerticalLinePaint().strokeWidth = 4f
    }

    private fun createChartModifiers() {
        verticalSliceModifier = VerticalSliceModifier()
        verticalSliceModifier!!.showTooltip = true
        verticalSliceModifier!!.showAxisLabels = false
        verticalSliceModifier!!.setDrawVerticalLine(true)
        verticalSliceModifier!!.isEnabled = false
        verticalSliceModifier!!.setReceiveHandledEvents(false)
        horizontalPinchZoomModifier = HorizontalPinchZoomModifier()
        pinchZoomModifier = PinchZoomModifier()
        twoFingerPanModifier = TwoFingerPanModifier()
        twoFingerPanModifier!!.setReceiveHandledEvents(true)
        doubleTapZoomDefaultModifier = DoubleTapZoomDefaultModifier()
        doubleTapZoomDefaultModifier!!.setReceiveHandledEvents(true)
    }

    private fun setDefaultChartModifiers() {
        val modifierList: MutableList<IChartModifier?> = ArrayList()
        modifierList.add(horizontalPinchZoomModifier)
        modifierList.add(doubleTapZoomDefaultModifier)
        modifierList.add(verticalSliceModifier)
        val chartModifiers = ChartModifierCollection(modifierList)
        chartSurface.chartModifiers = chartModifiers
    }

    private fun setFreeZoomingChartModifiers() {
        val modifierList: MutableList<IChartModifier?> = ArrayList()
        modifierList.add(pinchZoomModifier)
        modifierList.add(doubleTapZoomDefaultModifier)
        modifierList.add(verticalSliceModifier)
        modifierList.add(twoFingerPanModifier)
        val chartModifiers = ChartModifierCollection(modifierList)
        chartSurface.chartModifiers = chartModifiers
    }

    private fun initDataSeries() {
        dataSeries = XyDataSeries(Double::class.javaObjectType, Double::class.javaObjectType)
        for (i in 0 until maxNumberOfSamples) {
            val xValue = i / dataSamplingRateHz.toDouble()
            dataSeries!!.append(xValue, Double.NaN)
        }
    }

    private fun addValue(yValue: Double) {
        dataSeries!!.updateYAt(xAxisPositionCounter, yValue)
    }

    private fun addGap() {
        //check for roll over
        if (xAxisPositionCounter + gapSize > visibleNumberOfSamples) {
            //roll over
            fillDoubleValues(gapList, visibleNumberOfSamples - xAxisPositionCounter, Double.NaN)
            dataSeries!!.updateRangeYAt(xAxisPositionCounter, gapList)
            fillDoubleValues(
                gapList,
                xAxisPositionCounter + gapSize - visibleNumberOfSamples,
                Double.NaN
            )
            dataSeries!!.updateRangeYAt(0, gapList)
        } else {
            //no roll over
            fillDoubleValues(gapList, gapSize, Double.NaN)
            dataSeries!!.updateRangeYAt(xAxisPositionCounter, gapList)
        }
    }

    /**
     * Reuse DoubleValues list to avoid boxing / unboxing and heap trashing.
     */
    private fun fillDoubleValues(list: DoubleValues, length: Int, value: Double) {
        // ensure it has size long enough
        list.setSize(length)
        // get backing array ( need to do it after resizing in case if backing array was too small and was recreated )
        val array = list.itemsArray

        // fill values - in case if you reuse DoubleValue it's array could larger than requested size
        // because it keeps the array with largest size
        Arrays.fill(array, 0, length, value)
    }

    private fun increaseCounter() {
        xAxisPositionCounter++
        if (xAxisPositionCounter >= visibleNumberOfSamples) {
            xAxisPositionCounter = 0
        }
    }

    private fun getGapSize(): Int {
        return (visibleNumberOfSamples * 0.05).toInt()
    }

    private val maxGapSize: Int
        private get() = (maxNumberOfSamples * 0.05).toInt()

    companion object {
        private const val MIN_GAP_SIZE = 1
    }
}

