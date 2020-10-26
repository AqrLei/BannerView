package com.aqrlei.bannerview.widget.options

import com.aqrlei.bannerview.widget.enums.BannerIndicatorGravity
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2020/10/26
 */
class BannerIndicatorChildOptions  {

    var indicatorChildGravity: BannerIndicatorGravity = BannerIndicatorGravity.CENTER

    var indicatorChildGravityBias: Float = 0.5F
        get() = when {
            field < 0 -> 0F
            field > 1 -> 1F
            else -> field
        }

    var indicatorChildMargin: IndicatorChildMargin = IndicatorChildMargin()

    class IndicatorChildMargin(
        var start: Int = defaultMargin,
        var top: Int = defaultMargin,
        var end: Int = defaultMargin,
        var bottom: Int = defaultMargin) {
        companion object {
            val defaultMargin = BannerUtils.dp2px(10F).toInt()
        }

        fun paramChange(block: IndicatorChildMargin.() -> Unit) {
            this.apply(block)
        }
    }

}