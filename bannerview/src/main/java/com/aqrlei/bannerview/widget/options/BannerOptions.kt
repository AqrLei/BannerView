package com.aqrlei.bannerview.widget.options

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.enums.*
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2019-12-12
 */
private const val MIN_SCALE = 0.85F
const val DEFAULT_SCROLL_DURATION = 500

class BannerOptions {

    //仅在bannerView中的属性
    var scrollDuration: Int = DEFAULT_SCROLL_DURATION

    //仅在BannerView2中的属性
    var transformerStyle: TransformerStyle = TransformerStyle.NONE

    //以下字段只有在transformerStyle 设置为MULTI, MULTI_OVERLAP,SCALE_IN才有效果
    var transformerScale: Float = MIN_SCALE

    //以下字段只有在transformerStyle 设置为MULTI, MULTI_OVERLAP才有效果
    var offsetPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
    var revealWidth = BannerUtils.dp2px(20F)


    //通用的属性
    var interval: Int = 0
    var isLooping: Boolean = false
    var isCanLoop: Boolean = false
    var isAutoPlay: Boolean = false
    var indicatorGravity: IndicatorGravity = IndicatorGravity.CENTER
    var indicatorPosition: IndicatorPosition = IndicatorPosition.INSIDE
    var indicatorGravityBias: Float = 0.5F
        get() = when {
            field < 0 -> 0F
            field > 1 -> 1F
            else -> field
        }

    var indicatorVerticalBias: Float = 1.0F
        get() = when {
            field < 0 -> 0F
            field > 1 -> 1F
            else -> field
        }


    var indicatorMargin: IndicatorMargin = IndicatorMargin()
    var indicatorVisibility: Int = View.VISIBLE

    //banner宽高比
    var bannerUseRatio: Boolean = false
    var widthHeightRatio: String = ""
    var indicatorOptions: IndicatorOptions = IndicatorOptions()

    fun indicatorOptions(block: IndicatorOptions.() -> Unit) {
        indicatorOptions.apply(block)
    }

    val indicatorNormalColor: Int get() = indicatorOptions.normalColor
    val indicatorCheckedColor: Int get() = indicatorOptions.checkedColor
    val normalIndicatorWidth: Float get() = indicatorOptions.normalIndicatorWidth
    val checkedIndicatorWidth: Float get() = indicatorOptions.checkedIndicatorWidth
    val indicatorStyle: IndicatorStyle get() = indicatorOptions.indicatorStyle
    val slideMode: IndicatorSlideMode get() = indicatorOptions.slideMode
    val indicatorGap: Int get() = indicatorOptions.indicatorGap
    val indicatorHeight: Float get() = indicatorOptions.sliderHeight

    fun indicatorMargin(block: IndicatorMargin.() -> Unit) {
        indicatorMargin.paramChange(block)
    }

    class IndicatorMargin(
        var start: Int = defaultMargin,
        var top: Int = defaultMargin,
        var end: Int = defaultMargin,
        var bottom: Int = defaultMargin) {
        companion object {
            val defaultMargin = BannerUtils.dp2px(10F).toInt()
        }

        fun paramChange(block: IndicatorMargin.() -> Unit) {
            this.apply(block)
        }
    }
}