package com.aqrlei.bannerview.widget.indicator.drawer

import android.graphics.Canvas
import com.aqrlei.bannerview.widget.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
class CircleDrawer(indicatorOptions: IndicatorOptions) : BaseDrawer(indicatorOptions) {


    override fun onDrawIndicator(canvas: Canvas, pageSize: Int) {
        drawNormal(canvas)
        drawSlider(canvas)
    }

    private fun drawNormal(canvas: Canvas) {
        val normalIndicatorWidth = indicatorOptions.normalIndicatorWidth
        mPaint.color = indicatorOptions.normalColor
        for (i in 0 until indicatorOptions.pageSize) {
            val cx = maxWidth / 2 + (indicatorOptions.indicatorGap + normalIndicatorWidth) * i
            val cy = maxWidth / 2
            val radius = normalIndicatorWidth / 2
            drawCircle(canvas, cx, cy, radius)
        }
    }

    private fun drawSlider(canvas: Canvas) {
        mPaint.color = indicatorOptions.checkedColor
        when (indicatorOptions.slideMode) {
            IndicatorSlideMode.NORMAL, IndicatorSlideMode.SMOOTH -> drawCircleSlider(canvas)
            IndicatorSlideMode.WORM -> drawWormSlider(canvas)
        }
    }

    private fun drawCircleSlider(canvas: Canvas) {
        val normalIndicatorWidth = indicatorOptions.normalIndicatorWidth
        val indicatorGap = indicatorOptions.indicatorGap
        val currentPosition = indicatorOptions.currentPosition
        val slideProgress = indicatorOptions.slideProgress
        val cx =
            maxWidth / 2 + (normalIndicatorWidth + indicatorGap) * (currentPosition + slideProgress)
        val cy = maxWidth / 2F
        val radius = indicatorOptions.checkedIndicatorWidth / 2
        drawCircle(canvas, cx, cy, radius)
    }

    private fun drawWormSlider(canvas: Canvas) {
        val slideProgress = indicatorOptions.slideProgress
        val currentPosition = indicatorOptions.currentPosition
        val normalIndicatorWidth = indicatorOptions.normalIndicatorWidth
        val distance = indicatorOptions.indicatorGap + normalIndicatorWidth
        val cx =
            maxWidth / 2 + (normalIndicatorWidth + indicatorOptions.indicatorGap) * currentPosition
        val left =
            cx + (distance * (slideProgress - 0.5f) * 2.0f).coerceAtLeast(0f) - normalIndicatorWidth / 2
        val right =
            cx + (distance * slideProgress * 2.0f).coerceAtMost(distance) + normalIndicatorWidth / 2
        rectF.set(left, 0f, right, normalIndicatorWidth)
        canvas.drawRoundRect(rectF, normalIndicatorWidth, normalIndicatorWidth, mPaint)
    }

    private fun drawCircle(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        canvas.drawCircle(cx, cy, radius, mPaint)
    }
}