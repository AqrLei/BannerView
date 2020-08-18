package com.aqrlei.bannerview.widget.options

import android.graphics.Color
import com.aqrlei.bannerview.widget.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.enums.IndicatorStyle
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2019-12-11
 */
class IndicatorOptions {

    var customIndicator: Boolean = false

    var indicatorStyle = IndicatorStyle.DASH

    var pageSize: Int = 0

    var normalColor: Int = Color.parseColor("#8C18171C")

    var checkedColor: Int = Color.parseColor("#8C6C6D72")

    var indicatorGap: Int = 0

    var slideProgress: Float = 0F

    var currentPosition: Int = 0

    var sliderHeight: Float = 0F
        get() = if (field > 0) field else normalIndicatorWidth / 2

    var slideMode = IndicatorSlideMode.NORMAL

    var normalIndicatorWidth: Float = BannerUtils.dp2px(8F)

    var checkedIndicatorWidth: Float = BannerUtils.dp2px(8F)
}