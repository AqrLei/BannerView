package com.aqrlei.bannerview.widget.indicator.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.CallSuper
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.indicator.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
abstract class BaseIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IIndicator {

    protected var mIndicatorOptions: IndicatorOptions = IndicatorOptions()

    private var viewPager2: ViewPager2? = null
    private var viewPager: ViewPager? = null

    private var mOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            pageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            pageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            pageScrollStateChanged(state)
        }
    }

    val isCustomIndicator: Boolean get() = mIndicatorOptions.customIndicator

    var pageSize: Int
        get() = mIndicatorOptions.pageSize
        private set(value) {
            mIndicatorOptions.pageSize = value
        }

    var currentPosition: Int
        get() = mIndicatorOptions.currentPosition
        private set(value) {
            mIndicatorOptions.currentPosition = value
        }

    val indicatorGap: Int get() = mIndicatorOptions.indicatorGap

    var slideProgress: Float
        get() = mIndicatorOptions.slideProgress
        private set(value) {
            mIndicatorOptions.slideProgress = value
        }

    val slideMode: IndicatorSlideMode get() = mIndicatorOptions.slideMode

    val normalColor: Int get() = mIndicatorOptions.normalColor

    val checkedColor: Int get() = mIndicatorOptions.checkedColor

    val normalIndicatorWidth: Float get() = mIndicatorOptions.normalIndicatorWidth

    val checkedIndicatorWidth: Float get() = mIndicatorOptions.checkedIndicatorWidth

    fun refreshIndicatorOptions(block: IndicatorOptions.() -> Unit) {
        mIndicatorOptions.apply(block)
        notifyChanged()
    }

    fun setupWithViewPager(viewPager: ViewPager?) {
        this.viewPager = viewPager
        notifyChanged()
    }

    fun setupWithViewPager2(viewPager2: ViewPager2?) {
        this.viewPager2 = viewPager2
        notifyChanged()
    }

    @CallSuper
    override fun setIndicatorOptions(options: IndicatorOptions) {
        mIndicatorOptions = options
    }

    @CallSuper
    override fun notifyChanged() {
        setupViewPager()
        requestLayout()
        invalidate()
    }

    private fun setupViewPager() {
        if (viewPager2 != null) {
            viewPager2!!.unregisterOnPageChangeCallback(mOnPageChangeCallback)
            viewPager2!!.registerOnPageChangeCallback(mOnPageChangeCallback)
            viewPager2!!.adapter?.itemCount?.let { pageSize = it }
        } else if (viewPager != null) {
            viewPager!!.removeOnPageChangeListener(this)
            viewPager!!.addOnPageChangeListener(this)
            viewPager!!.adapter?.count?.let { pageSize = it }
        }
    }

    override fun onPageSelected(position: Int) {
        pageSelected(position)
    }

    private fun pageSelected(position: Int) {
        if (slideMode == IndicatorSlideMode.NORMAL || isCustomIndicator) {
            currentPosition = position
            slideProgress = 0f
            invalidate()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        pageScrolled(position, positionOffset, positionOffsetPixels)
    }

    private fun pageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (slideMode in arrayOf(
                IndicatorSlideMode.SMOOTH,
                IndicatorSlideMode.WORM
            ) && pageSize > 1 && positionOffset != 0F && !isCustomIndicator
        ) {
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

    override fun onPageScrollStateChanged(state: Int) {
        pageScrollStateChanged(state)
    }

    private fun pageScrollStateChanged(state: Int) {}

}