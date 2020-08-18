package com.aqrlei.bannerview.widget.indicator.drawer

import android.graphics.Canvas

/**
 * created by AqrLei on 2019-12-11
 */
interface IDrawer {
    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)

    fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): MeasureResult
    fun onDraw(canvas: Canvas)

    data class MeasureResult(var measureWidth: Int = 0, var measureHeight: Int = 0) {
        fun setMeasureResult(width: Int, height: Int) {
            measureWidth = width
            measureHeight = height
        }
    }
}