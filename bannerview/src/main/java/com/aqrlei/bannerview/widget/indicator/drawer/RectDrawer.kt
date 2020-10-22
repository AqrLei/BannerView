package com.aqrlei.bannerview.widget.indicator.drawer

import android.graphics.Canvas
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.indicator.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
open class RectDrawer internal constructor(indicatorOptions: IndicatorOptions) :
    BaseDrawer(indicatorOptions) {

    override fun measureHeight(): Int {
        return indicatorOptions.sliderHeight.toInt()
    }

    override fun onDrawIndicator(canvas: Canvas, pageSize: Int) {
        if (checkWidthEqual() && indicatorOptions.slideMode != IndicatorSlideMode.NORMAL) {
            drawUncheckedSlide(canvas, pageSize)
            drawCheckedSlider(canvas)
        } else {
            drawUnEqual(canvas, pageSize)
        }
    }

    private fun drawUncheckedSlide(canvas: Canvas, pageSize: Int) {
        mPaint.color = indicatorOptions.normalColor
        for (i in 0 until pageSize) {
            val sliderHeight = indicatorOptions.sliderHeight
            val left = i * (maxWidth + indicatorOptions.indicatorGap) + (maxWidth - minWidth)
            val right = left + minWidth
            rectF.set(left, 0F, right, sliderHeight)
            drawRoundRect(canvas, sliderHeight, sliderHeight)
        }
    }

    private fun drawCheckedSlider(canvas: Canvas) {
        mPaint.color = indicatorOptions.checkedColor
        when (indicatorOptions.slideMode) {
            IndicatorSlideMode.SMOOTH -> drawSmoothSlider(canvas)
            IndicatorSlideMode.WORM -> drawWormSlider(canvas)
            IndicatorSlideMode.NORMAL -> { }
        }
    }

    private fun drawSmoothSlider(canvas: Canvas) {
        val currentPosition = indicatorOptions.currentPosition
        val indicatorGap = indicatorOptions.indicatorGap
        val left =
            currentPosition * (maxWidth + indicatorGap) + (maxWidth + indicatorGap) * indicatorOptions.slideProgress
        rectF.set(left, 0F, left + maxWidth, indicatorOptions.sliderHeight)
        drawRoundRect(canvas, indicatorOptions.sliderHeight, indicatorOptions.sliderHeight)
    }

    private fun drawWormSlider(canvas: Canvas) {
        val sliderHeight = indicatorOptions.sliderHeight
        val slideProgress = indicatorOptions.slideProgress
        val currentPosition = indicatorOptions.currentPosition
        val distance = indicatorOptions.indicatorGap + indicatorOptions.normalIndicatorWidth
        val normalIndicatorWidth = indicatorOptions.normalIndicatorWidth
        val cx =
            maxWidth / 2 + (normalIndicatorWidth + indicatorOptions.indicatorGap) * currentPosition
        val left =
            cx + (distance * (slideProgress - 0.5f) * 2.0f).coerceAtLeast(0f) - normalIndicatorWidth / 2
        val right =
            cx + (distance * slideProgress * 2f).coerceAtMost(distance) + normalIndicatorWidth / 2

        rectF.set(left, 0f, right, sliderHeight)
        drawRoundRect(canvas, sliderHeight, sliderHeight)
    }

    private fun drawUnEqual(canvas: Canvas, pageSize: Int) {
        val isChecked = { position : Int   -> position == indicatorOptions.currentPosition }
        var left = 0F
        for (position in 0 until pageSize) {
            val sliderWidth = if (isChecked(position)) maxWidth else minWidth
            mPaint.color = if (isChecked(position)) indicatorOptions.checkedColor else indicatorOptions.normalColor
            val right = if (isChecked(position)) left + minWidth else left + maxWidth
            rectF.set(left, 0F, right, indicatorOptions.sliderHeight)
            drawRoundRect(canvas, indicatorOptions.sliderHeight, indicatorOptions.sliderHeight)
            left += sliderWidth +indicatorOptions.indicatorGap
        }
    }

    protected open fun drawRoundRect(canvas: Canvas, rx: Float, ry: Float) {
        drawDash(canvas)
    }

    protected open fun drawDash(canvas: Canvas) {}
}