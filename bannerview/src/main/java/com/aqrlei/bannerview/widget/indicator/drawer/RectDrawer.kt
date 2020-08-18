package com.aqrlei.bannerview.widget.indicator.drawer

import android.graphics.Canvas
import com.aqrlei.bannerview.widget.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
open class RectDrawer internal constructor(indicatorOptions: IndicatorOptions)
    : BaseDrawer(indicatorOptions) {

    override fun measureHeight(): Int {
        return indicatorOptions.sliderHeight.toInt()
    }

    override fun onDrawIndicator(canvas: Canvas, pageSize: Int) {
        if (checkWidthEqual() && indicatorOptions.slideMode != IndicatorSlideMode.NORMAL) {
            drawUncheckedSlide(canvas)
            drawCheckedSlider(canvas)
        } else {
            for (i in 0 until pageSize) {
                drawUnEqual(canvas, i)
            }
        }
    }

    private fun drawUncheckedSlide(canvas: Canvas) {
        mPaint.color = indicatorOptions.normalColor
        for (i in 0 until indicatorOptions.pageSize) {
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
            else -> {
            }
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

    private fun drawUnEqual(canvas: Canvas, position: Int) {
        val normalColor = indicatorOptions.normalColor
        val indicatorGap = indicatorOptions.indicatorGap
        val sliderHeight = indicatorOptions.sliderHeight
        val currentPosition = indicatorOptions.currentPosition
        when {
            position < currentPosition -> {
                mPaint.color = normalColor
                val left = position * (minWidth + indicatorGap)
                rectF.set(left, 0F, left + minWidth, sliderHeight)
                drawRoundRect(canvas, sliderHeight, sliderHeight)
            }
            position == currentPosition -> {
                mPaint.color = indicatorOptions.checkedColor
                val left = position * (minWidth + indicatorGap)
                rectF.set(left, 0F, left + maxWidth, sliderHeight)
                drawRoundRect(canvas, sliderHeight, sliderHeight)
            }
            else -> {
                mPaint.color = normalColor
                val left = position * (minWidth + indicatorGap) + maxWidth - minWidth
                rectF.set(left, 0F, left + minWidth, sliderHeight)
                drawRoundRect(canvas, sliderHeight, sliderHeight)
            }
        }
    }

    protected open fun drawRoundRect(canvas: Canvas, rx: Float, ry: Float) {
        drawDash(canvas)
    }

    protected open fun drawDash(canvas: Canvas) {}
}