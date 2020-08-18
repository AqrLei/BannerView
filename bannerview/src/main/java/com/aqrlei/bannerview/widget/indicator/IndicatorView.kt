package com.aqrlei.bannerview.widget.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.aqrlei.bannerview.widget.indicator.drawer.DrawerProxy
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-12
 */
class IndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : BaseIndicatorView(context, attrs, defStyleAttr) {

    private val drawerProxy: DrawerProxy = DrawerProxy(indicatorOptions)

    override fun onIndicatorOptionsSet(options: IndicatorOptions) {
        drawerProxy.setIndicatorOptions(options)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measureResult = drawerProxy.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureResult.measureWidth, measureResult.measureHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { drawerProxy.onDraw(canvas) }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        drawerProxy.onLayout(changed, left, top, right, bottom)
    }
}