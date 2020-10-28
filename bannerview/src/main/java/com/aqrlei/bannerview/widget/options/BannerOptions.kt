package com.aqrlei.bannerview.widget.options

import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.enums.*
import com.aqrlei.bannerview.widget.indicator.options.IndicatorOptions
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2019-12-12
 */
internal const val MIN_SCALE = 0.85F

class BannerOptions {

    var orientation: Int = ViewPager2.ORIENTATION_HORIZONTAL

    var pageTransformerStyle: PageTransformerStyle = PageTransformerStyle.NORMAL

    var transformerScale: Float = MIN_SCALE

    var offsetPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

    // 左或上的页面reveal的长度
    var startRevealWidth = 0F

    // 右或下的页面reveal的长度
    var endRevealWidth = 0F

    // 页面间的margin
    var pageMargin = 0F

    //自动轮播时间间隔
    var interval: Int = 0
    internal set

    // 是否正在自动循环播放
    var isLooping: Boolean = false
    internal set

    // 是否启用循环
    var isCanLoop: Boolean = false
    internal set

    // 是否启用自动轮播
    var isAutoPlay: Boolean = false
    internal set

    //是否使用banner宽高比
    var bannerUseRatio: Boolean = false
    internal set
    // banner宽高比
    var widthHeightRatio: String = ""
    internal set

    var indicatorOptions: IndicatorOptions = IndicatorOptions()
    internal set

    var bannerIndicatorParentOptions : BannerIndicatorParentOptions = BannerIndicatorParentOptions()
    internal set

    var bannerIndicatorChildOptions: BannerIndicatorChildOptions = BannerIndicatorChildOptions()
    internal set
}