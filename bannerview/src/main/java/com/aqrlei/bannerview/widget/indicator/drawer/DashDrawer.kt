package com.aqrlei.bannerview.widget.indicator.drawer

import android.graphics.Canvas
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
class DashDrawer(indicatorOptions: IndicatorOptions)
    : RectDrawer(indicatorOptions) {

    override fun drawDash(canvas: Canvas) {
        canvas.drawRect(rectF, mPaint)
    }
}