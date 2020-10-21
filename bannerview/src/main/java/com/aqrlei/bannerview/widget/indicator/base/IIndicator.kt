package com.aqrlei.bannerview.widget.indicator.base

import androidx.viewpager.widget.ViewPager
import com.aqrlei.bannerview.widget.indicator.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
interface IIndicator : ViewPager.OnPageChangeListener {

    fun setIndicatorOptions(options: IndicatorOptions)
    fun notifyChanged()
}