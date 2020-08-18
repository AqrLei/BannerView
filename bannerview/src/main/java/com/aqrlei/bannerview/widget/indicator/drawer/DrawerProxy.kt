package com.aqrlei.bannerview.widget.indicator.drawer

import android.graphics.Canvas
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
class DrawerProxy(indicatorOptions: IndicatorOptions) : IDrawer {

    private var drawer = DrawerFactory.createDrawer(indicatorOptions)
    fun setIndicatorOptions(indicatorOptions: IndicatorOptions) {
        drawer = DrawerFactory.createDrawer(indicatorOptions)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): IDrawer.MeasureResult {
        return drawer.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        drawer.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {}

}