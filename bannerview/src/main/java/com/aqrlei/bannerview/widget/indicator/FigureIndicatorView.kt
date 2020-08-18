package com.aqrlei.bannerview.widget.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2020/4/24
 */
class FigureIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : BaseIndicatorView(context, attrs, defStyleAttr) {

    var textColor: Int = Color.WHITE
    var textSize: Int = BannerUtils.dp2px(10F).toInt()

    var cornerRadius: Int = BannerUtils.dp2px(9F).toInt()
    var indicatorBackgroundColor: Int = Color.parseColor("#bbbbbb")

    private var lastContentSize: Int = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
    }
    private val rectF = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val horizontalContentSize = paint.measureText("${currentPosition + 1}/$pageSize").toInt()
        lastContentSize = horizontalContentSize
        val horizontalSize = paddingLeft + paddingRight + horizontalContentSize
        val verticalSize = paddingTop + paddingBottom + textSize
        setMeasuredDimension(horizontalSize, verticalSize)
    }

    override fun onDraw(canvas: Canvas?) {
        if (pageSize > 1) {
            paint.color = indicatorBackgroundColor
            rectF.set(0F, 0F, width.toFloat(), height.toFloat())
            canvas?.drawRoundRect(rectF, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)

            paint.color = textColor
            paint.textSize = textSize.toFloat()
            val text: String = "${currentPosition + 1}/${pageSize}"
            val textWidth = paint.measureText(text).toInt()
            if (textWidth != lastContentSize) {
                requestLayout()
                return
            }
            val fontMetricsInt: Paint.FontMetricsInt = paint.fontMetricsInt
            val baseline = (measuredHeight - (fontMetricsInt.bottom + fontMetricsInt.top)) / 2
            canvas?.drawText(text, ((width - textWidth) / 2).toFloat(), baseline.toFloat(), paint)
        }
    }
}