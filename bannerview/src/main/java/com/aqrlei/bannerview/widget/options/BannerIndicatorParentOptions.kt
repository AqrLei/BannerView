package com.aqrlei.bannerview.widget.options

import android.view.View
import com.aqrlei.bannerview.widget.enums.BannerIndicatorPosition

/**
 * created by AqrLei on 2020/10/26
 */
class BannerIndicatorParentOptions {

    var bannerIndicatorParentPosition: BannerIndicatorPosition = BannerIndicatorPosition.INSIDE

    // 指示器在banner内部时， 在父容器垂直方向的Bias
    var indicatorParentVerticalBias: Float = 1.0F
        get() = when {
            field < 0 -> 0F
            field > 1 -> 1F
            else -> field
        }

    var indicatorVisibility: Int = View.VISIBLE

}