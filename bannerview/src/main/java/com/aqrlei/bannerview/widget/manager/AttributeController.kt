package com.aqrlei.bannerview.widget.manager

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.R
import com.aqrlei.bannerview.widget.enums.*
import com.aqrlei.bannerview.widget.indicator.enums.*
import com.aqrlei.bannerview.widget.options.BannerIndicatorChildOptions
import com.aqrlei.bannerview.widget.options.BannerOptions
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2019-12-12
 */

class AttributeController(private val bannerOptions: BannerOptions) {

    companion object {
        private val defaultCheckedColor = Color.parseColor("#8C18171C")
        private val defaultNormalColor = Color.parseColor("#8C6C6D72")
        private const val DEFAULT_INTERVAL = 1000
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
            bannerOptions.indicatorOptions.run {  
                checkedColor = getColor(
                    R.styleable.BannerView_banner_indicator_checked_color,
                    defaultCheckedColor
                )
                normalColor = getColor(
                    R.styleable.BannerView_banner_indicator_normal_color,
                    defaultNormalColor
                )
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
                    R.styleable.BannerView_banner_indicator_checked_width,
                    normalIndicatorWidth)
            }
        }
    }

    private fun initBannerAttrs(typedArray: TypedArray) {
        with(typedArray) {
            initBannerIndicatorAttrs(typedArray)
            bannerOptions.run {
                bannerUseRatio = getBoolean(R.styleable.BannerView_banner_use_ratio, false)
                if (bannerUseRatio) {
                    widthHeightRatio = getString(R.styleable.BannerView_widthHeightRatio) ?: ""
                }
                interval = getInteger(R.styleable.BannerView_interval, DEFAULT_INTERVAL)
                isAutoPlay = getBoolean(R.styleable.BannerView_isAuto, false)
                isCanLoop = getBoolean(R.styleable.BannerView_banner_can_loop, false)
                revealWidth = getDimension(R.styleable.BannerView_banner_reveal_width, 0f)
                transformerStyle = TransformerStyle.values()[getInt(R.styleable.BannerView_banner_transformer_style, 0)]

                offsetPageLimit = getInt(
                    R.styleable.BannerView_banner_offset_page_limit,
                    ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT)

                transformerScale = getFloat(R.styleable.BannerView_banner_transformer_scale, 0.85F)
            }
        }
    }

    private fun initBannerIndicatorAttrs(typedArray: TypedArray) {
        with(typedArray){
            bannerOptions.bannerIndicatorParentOptions.run {

                bannerIndicatorParentPosition = BannerIndicatorPosition.values()[getInt(
                    R.styleable.BannerView_banner_indicator_parent_position, 0)]

                indicatorParentVerticalBias = getFloat(R.styleable.BannerView_banner_indicator_vertical_parent_bias,1.0F)

                indicatorVisibility =
                    getInt(R.styleable.BannerView_banner_indicator_visibility, View.VISIBLE)

            }

            bannerOptions.bannerIndicatorChildOptions.run {
                indicatorChildGravity = BannerIndicatorGravity.values()[getInt(
                    R.styleable.BannerView_banner_indicator_child_gravity, 0)]

                if (indicatorChildGravity == BannerIndicatorGravity.BIAS) {
                    indicatorChildGravityBias =
                        getFloat(R.styleable.BannerView_banner_indicator_child_bias, 0.5F)
                }

                val indicatorMarginStart = getDimension(
                    R.styleable.BannerView_banner_indicator_marginStart,
                    BannerIndicatorChildOptions.IndicatorChildMargin.defaultMargin.toFloat())
                val indicatorMarginTop =
                    getDimension(
                        R.styleable.BannerView_banner_indicator_marginTop,
                        BannerIndicatorChildOptions.IndicatorChildMargin.defaultMargin.toFloat())
                val indicatorMarginEnd =
                    getDimension(
                        R.styleable.BannerView_banner_indicator_marginEnd,
                        BannerIndicatorChildOptions.IndicatorChildMargin.defaultMargin.toFloat())
                val indicatorMarginBottom =
                    getDimension(
                        R.styleable.BannerView_banner_indicator_marginBottom,
                        BannerIndicatorChildOptions.IndicatorChildMargin.defaultMargin.toFloat())
                indicatorChildMargin.paramChange {
                    start = indicatorMarginStart.toInt()
                    top = indicatorMarginTop.toInt()
                    end = indicatorMarginEnd.toInt()
                    bottom = indicatorMarginBottom.toInt()
                }
            }
        }
    }
}