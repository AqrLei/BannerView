package com.aqrlei.bannerview.widget.indicator.drawer

import android.graphics.Canvas
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
class RoundRectDrawer(indicatorOptions: IndicatorOptions)
    : RectDrawer(indicatorOptions) {
    override fun drawRoundRect(canvas: Canvas, rx: Float, ry: Float) {
        canvas.drawRoundRect(rectF, rx, ry, mPaint)
    }
}