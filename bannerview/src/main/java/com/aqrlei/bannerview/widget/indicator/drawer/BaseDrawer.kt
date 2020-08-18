package com.aqrlei.bannerview.widget.indicator.drawer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
abstract class BaseDrawer(protected var indicatorOptions: IndicatorOptions) : IDrawer {

    protected val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    protected val measureResult = IDrawer.MeasureResult()

    protected val rectF = RectF()


    protected var maxWidth: Float = 0F
    protected var minWidth: Float = 0F

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): IDrawer.MeasureResult {
        maxWidth =
            indicatorOptions.normalIndicatorWidth.coerceAtLeast(indicatorOptions.checkedIndicatorWidth)
        minWidth =
            indicatorOptions.normalIndicatorWidth.coerceAtMost(indicatorOptions.checkedIndicatorWidth)
        measureResult.setMeasureResult(measureWidth(), measureHeight())
        return measureResult
    }

    final override fun onDraw(canvas: Canvas) {
        if (indicatorOptions.pageSize > 1) {
            onDrawIndicator(canvas, indicatorOptions.pageSize)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

    }

    protected open fun measureHeight(): Int {
        return maxWidth.toInt()
    }

    protected open fun measureWidth(): Int {
        val pageSize = indicatorOptions.pageSize
        val indicatorGap = indicatorOptions.indicatorGap
        return ((pageSize - 1) * indicatorGap + maxWidth + (pageSize - 1) * minWidth).toInt()
    }

    protected fun checkWidthEqual(): Boolean {
        return indicatorOptions.normalIndicatorWidth == indicatorOptions.checkedIndicatorWidth
    }

    abstract fun onDrawIndicator(canvas: Canvas, pageSize: Int)
}