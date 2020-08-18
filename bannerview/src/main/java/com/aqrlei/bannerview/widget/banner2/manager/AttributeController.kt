package com.aqrlei.bannerview.widget.banner2.manager

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.R
import com.aqrlei.bannerview.widget.enums.*
import com.aqrlei.bannerview.widget.options.BannerOptions
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
            context.obtainStyledAttributes(attrs, R.styleable.BannerView2).apply {
                initBannerAttrs(this)
                initIndicatorAttrs(this)
                recycle()
            }
        }
    }

    private fun initIndicatorAttrs(typedArray: TypedArray) {
        with(typedArray) {
            bannerOptions.indicatorOptions {
                checkedColor = getColor(
                    R.styleable.BannerView2_banner_indicator_checked_color,
                    defaultCheckedColor)
                normalColor = getColor(
                    R.styleable.BannerView2_banner_indicator_normal_color,
                    defaultNormalColor)
                normalIndicatorWidth = getDimension(
                    R.styleable.BannerView2_banner_indicator_normal_width,
                    BannerUtils.dp2px(8f))
                indicatorStyle = IndicatorStyle.values()[(getInt(
                    R.styleable.BannerView2_banner_indicator_style,
                    0))]

                slideMode = IndicatorSlideMode.values()[getInt(
                    R.styleable.BannerView2_banner_indicator_slide_mode,
                    0)]

                indicatorGap = getDimension(
                    R.styleable.BannerView2_banner_indicator_gap,
                    normalIndicatorWidth).toInt()
                sliderHeight = getDimension(
                    R.styleable.BannerView2_banner_indicator_height,
                    normalIndicatorWidth / 2)
                checkedIndicatorWidth = getDimension(
                    R.styleable.BannerView2_banner_indicator_checked_width,
                    normalIndicatorWidth)
            }
        }
    }

    private fun initBannerAttrs(typedArray: TypedArray) {
        typedArray.let {
            bannerOptions.apply {
                indicatorGravity = IndicatorGravity.values()[it.getInt(
                    R.styleable.BannerView2_banner_indicator_gravity, 0)]
                indicatorPosition = IndicatorPosition.values()[it.getInt(
                    R.styleable.BannerView2_banner_indicator_position, 0)]

                if (indicatorGravity == IndicatorGravity.BIAS) {
                    indicatorGravityBias =
                        it.getFloat(R.styleable.BannerView2_banner_indicator_bias, 0.5F)
                }
                indicatorVerticalBias = it.getFloat(R.styleable.BannerView2_banner_vertical_indicator_bias,1.0F)

                indicatorVisibility =
                    it.getInt(R.styleable.BannerView2_banner_indicator_visibility, View.VISIBLE)

                val indicatorMarginStart = it.getDimension(
                    R.styleable.BannerView2_banner_indicator_marginStart,
                    BannerOptions.IndicatorMargin.defaultMargin.toFloat())
                val indicatorMarginTop =
                    it.getDimension(
                        R.styleable.BannerView2_banner_indicator_marginTop,
                        BannerOptions.IndicatorMargin.defaultMargin.toFloat())
                val indicatorMarginEnd =
                    it.getDimension(
                        R.styleable.BannerView2_banner_indicator_marginEnd,
                        BannerOptions.IndicatorMargin.defaultMargin.toFloat())
                val indicatorMarginBottom =
                    it.getDimension(
                        R.styleable.BannerView2_banner_indicator_marginBottom,
                        BannerOptions.IndicatorMargin.defaultMargin.toFloat())
                indicatorMargin.paramChange {
                    start = indicatorMarginStart.toInt()
                    top = indicatorMarginTop.toInt()
                    end = indicatorMarginEnd.toInt()
                    bottom = indicatorMarginBottom.toInt()
                }
                bannerUseRatio = it.getBoolean(R.styleable.BannerView2_banner_use_ratio, false)
                if (bannerUseRatio) {
                    widthHeightRatio =
                        it.getString(R.styleable.BannerView2_widthHeightRatio) ?: ""
                }
                interval = it.getInteger(R.styleable.BannerView2_interval, DEFAULT_INTERVAL)
                isAutoPlay = it.getBoolean(R.styleable.BannerView2_isAuto, true)
                isCanLoop = it.getBoolean(R.styleable.BannerView2_banner_can_loop, true)
                revealWidth = it.getDimension(R.styleable.BannerView2_banner_reveal_width, 0f)
                transformerStyle = TransformerStyle.values()[it.getInt(
                    R.styleable.BannerView2_banner_transformer_style,
                    0)]

                offsetPageLimit = it.getInt(
                    R.styleable.BannerView2_banner_offset_page_limit,
                    ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT)

                transformerScale =
                    it.getFloat(R.styleable.BannerView2_banner_transformer_scale, 0.85F)
            }
        }
    }
}