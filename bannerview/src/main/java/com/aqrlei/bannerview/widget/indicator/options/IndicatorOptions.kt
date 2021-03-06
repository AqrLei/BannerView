package com.aqrlei.bannerview.widget.indicator.options

import android.graphics.Color
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorStyle
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2019-12-11
 */
class IndicatorOptions {

    var pageSize: Int = 0
        internal set

    var currentPosition: Int = 0
        internal set

    var slideProgress: Float = 0F
        internal set

    var indicatorStyle = IndicatorStyle.ROUND_RECT

    var slideMode = IndicatorSlideMode.NORMAL

    var indicatorGap: Int = 0

    var sliderHeight: Float = 0F
        get() = if (field > 0) field else normalIndicatorWidth / 2

    var normalColor: Int = Color.parseColor("#8C18171C")

    var checkedColor: Int = Color.parseColor("#8C6C6D72")

    var normalIndicatorWidth: Float = BannerUtils.dp2px(8F)

    var checkedIndicatorWidth: Float = BannerUtils.dp2px(8F)
}