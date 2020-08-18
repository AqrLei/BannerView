package com.aqrlei.bannerview.widget.indicator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.aqrlei.bannerview.widget.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
abstract class BaseIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr), IIndicator {
    override var indicatorOptions: IndicatorOptions =
        IndicatorOptions()
        set(value) {
            field = value
            onIndicatorOptionsSet(field)
        }

    open fun onIndicatorOptionsSet(options: IndicatorOptions) {}

    val pageSize: Int get() = indicatorOptions.pageSize

    val normalColor: Int get() = indicatorOptions.normalColor

    val checkedColor: Int get() = indicatorOptions.checkedColor

    val indicatorGap: Int get() = indicatorOptions.indicatorGap

    val slideProgress: Float get() = indicatorOptions.slideProgress

    val currentPosition: Int get() = indicatorOptions.currentPosition

    val slideMode: IndicatorSlideMode get() = indicatorOptions.slideMode

    val isCustomIndicator: Boolean get() = indicatorOptions.customIndicator

    val normalIndicatorWidth: Float get() = indicatorOptions.normalIndicatorWidth

    val checkedIndicatorWidth: Float get() = indicatorOptions.checkedIndicatorWidth

    private fun changeIndicatorOptions(block: IndicatorOptions.() -> Unit) {
        indicatorOptions.apply(block)
    }

    override fun onPageSelected(position: Int) {
        changeIndicatorOptions {
            if (slideMode == IndicatorSlideMode.NORMAL || customIndicator) {
                currentPosition = position
                slideProgress = 0f
                invalidate()
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (slideMode in arrayOf(
                IndicatorSlideMode.SMOOTH,
                IndicatorSlideMode.WORM) && pageSize > 1 && positionOffset != 0F && !isCustomIndicator) {
            changeIndicatorOptions {
                if (position % pageSize == pageSize - 1) {
                    currentPosition = if (positionOffset < 0.5) position else 0
                    slideProgress = 0F
                } else {
                    currentPosition = position
                    slideProgress = positionOffset
                }
                invalidate()
            }
        }
    }

    override fun setPageSize(pageSize: Int) {
        changeIndicatorOptions { this.pageSize = pageSize }
        requestLayout()
    }

    override fun onPageScrollStateChanged(state: Int) {}

}