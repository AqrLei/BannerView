package com.aqrlei.bannerview.widget.options

import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.enums.*
import com.aqrlei.bannerview.widget.indicator.options.IndicatorOptions
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2019-12-12
 */
private const val MIN_SCALE = 0.85F

class BannerOptions {
    
    var transformerStyle: TransformerStyle = TransformerStyle.NONE

    //以下字段只有在transformerStyle 设置为MULTI, MULTI_OVERLAP,SCALE_IN才有效果
    var transformerScale: Float = MIN_SCALE

    //以下字段只有在transformerStyle 设置为MULTI, MULTI_OVERLAP才有效果
    var offsetPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

    var revealWidth = BannerUtils.dp2px(20F)


    //自动轮播时间间隔
    var interval: Int = 0

    // 是否正在自动循环播放
    var isLooping: Boolean = false

    // 是否启用循环
    var isCanLoop: Boolean = false

    // 是否启用自动轮播
    var isAutoPlay: Boolean = false

    //banner宽高比
    var bannerUseRatio: Boolean = false

    var widthHeightRatio: String = ""


    var indicatorOptions: IndicatorOptions = IndicatorOptions()
    internal set

    var bannerIndicatorParentOptions : BannerIndicatorParentOptions = BannerIndicatorParentOptions()
    internal set

    var bannerIndicatorChildOptions: BannerIndicatorChildOptions = BannerIndicatorChildOptions()
    internal set
}