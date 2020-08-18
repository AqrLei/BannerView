package com.aqrlei.bannerview.widget.manager

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.aqrlei.bannerview.widget.R
import com.aqrlei.bannerview.widget.enums.*
import com.aqrlei.bannerview.widget.options.BannerOptions
import com.aqrlei.bannerview.widget.options.DEFAULT_SCROLL_DURATION
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2019-12-12
 */

class AttributeController(private val bannerOptions: BannerOptions) {

    companion object {
        private val defaultCheckedColor = Color.parseColor("#8C18171C")
        private val defaultNormalColor = Color.parseColor("#8C6C6D72")
        private const val DEFAULT_INTERVAL = 3000
    }

    fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.BannerView).apply {
                initBannerAttrs(this)
                initIndicatorAttrs(this)
                recycle()
            }
        }
    }

    private fun initIndicatorAttrs(typedArray: TypedArray) {
        with(typedArray) {
            bannerOptions
                .also {
                    it.indicatorGravity = IndicatorGravity.values()[getInt(
                        R.styleable.BannerView_banner_indicator_gravity, 0)]

                    it.indicatorPosition = IndicatorPosition.values()[getInt(
                        R.styleable.BannerView_banner_indicator_position, 0)]
                    if (it.indicatorGravity == IndicatorGravity.BIAS) {
                        it.indicatorGravityBias =
                            getFloat(R.styleable.BannerView_banner_indicator_bias, 0.5F)
                    }
                    it.indicatorVerticalBias = getFloat(R.styleable.BannerView_banner_vertical_indicator_bias,1.0F)
                    it.indicatorVisibility =
                        getInt(R.styleable.BannerView_banner_indicator_visibility, View.VISIBLE)

                    val indicatorMarginStart = getDimension(
                        R.styleable.BannerView_banner_indicator_marginStart,
                        BannerOptions.IndicatorMargin.defaultMargin.toFloat())
                    val indicatorMarginTop =
                        getDimension(
                            R.styleable.BannerView_banner_indicator_marginTop,
                            BannerOptions.IndicatorMargin.defaultMargin.toFloat())
                    val indicatorMarginEnd =
                        getDimension(
                            R.styleable.BannerView_banner_indicator_marginEnd,
                            BannerOptions.IndicatorMargin.defaultMargin.toFloat())
                    val indicatorMarginBottom =
                        getDimension(
                            R.styleable.BannerView_banner_indicator_marginBottom,
                            BannerOptions.IndicatorMargin.defaultMargin.toFloat())
                    it.indicatorMargin.paramChange {
                        start = indicatorMarginStart.toInt()
                        top = indicatorMarginTop.toInt()
                        end = indicatorMarginEnd.toInt()
                        bottom = indicatorMarginBottom.toInt()
                    }
                    it.bannerUseRatio = getBoolean(R.styleable.BannerView_banner_use_ratio, false)
                    if (it.bannerUseRatio) {
                        it.widthHeightRatio =
                            getString(R.styleable.BannerView_widthHeightRatio) ?: ""
                    }
                }
                .indicatorOptions {
                    checkedColor = getColor(
                        R.styleable.BannerView_banner_indicator_checked_color,
                        defaultCheckedColor)
                    normalColor = getColor(
                        R.styleable.BannerView_banner_indicator_normal_color,
                        defaultNormalColor)
                    normalIndicatorWidth = getDimension(
                        R.styleable.BannerView_banner_indicator_normal_width,
                        BannerUtils.dp2px(8f))
                    indicatorStyle = IndicatorStyle.values()[(getInt(
                        R.styleable.BannerView_banner_indicator_style,
                        0))]

                    slideMode = IndicatorSlideMode.values()[getInt(
                        R.styleable.BannerView_banner_indicator_slide_mode,
                        0)]

                    indicatorGap = getDimension(
                        R.styleable.BannerView_banner_indicator_gap,
                        normalIndicatorWidth).toInt()
                    sliderHeight = getDimension(
                        R.styleable.BannerView_banner_indicator_height,
                        normalIndicatorWidth / 2)
                    checkedIndicatorWidth = getDimension(
                        R.styleable.BannerView_banner_indicator_checked_width, normalIndicatorWidth)
                }
        }
    }

    private fun initBannerAttrs(typedArray: TypedArray) {
        typedArray.let {
            bannerOptions.apply {
                widthHeightRatio = it.getString(R.styleable.BannerView_widthHeightRatio) ?: ""
                interval = it.getInteger(R.styleable.BannerView_interval, DEFAULT_INTERVAL)
                isAutoPlay = it.getBoolean(R.styleable.BannerView_isAuto, true)
                isCanLoop = it.getBoolean(R.styleable.BannerView_banner_can_loop, true)
                scrollDuration = it.getInt(
                    R.styleable.BannerView_banner_scroll_duration,
                    DEFAULT_SCROLL_DURATION)
            }
        }
    }
}