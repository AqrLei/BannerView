package com.aqrlei.bannerview.widget.indicator

import com.aqrlei.bannerview.widget.options.IndicatorOptions
import com.aqrlei.bannerview.widget.page.OnBannerPageChangeListener

/**
 * created by AqrLei on 2019-12-11
 */
interface IIndicator : OnBannerPageChangeListener {
    var indicatorOptions: IndicatorOptions
    fun setPageSize(pageSize: Int)
}