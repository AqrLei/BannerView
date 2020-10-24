package com.aqrlei.bannerview.sample.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.TypedArrayUtils
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.sample.R
import com.aqrlei.bannerview.sample.holder.SimpleBannerViewHolder
import com.aqrlei.bannerview.widget.BannerView
import com.aqrlei.bannerview.widget.enums.BannerIndicatorPosition
import com.aqrlei.bannerview.widget.indicator.FigureIndicatorView
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorStyle
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_pager_indicator.*

/**
 * created by AqrLei on 2020/10/20
 */
class IndicatorFragment : Fragment() {
    companion object {
        fun create() = IndicatorFragment()
    }

    private var dp6 : Int = 3

    private val bannerItemArray = ArrayList<Int>().also {
        it.addAll(arrayOf(Color.GREEN, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.RED))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dp6 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6F, context!!.resources.displayMetrics).toInt()
        return inflater.inflate(R.layout.fragment_pager_indicator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(bvBanner) {
            pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d(BannerView.TAG, "position $position was selected")
                }
            }
            pageClickCallback = object : BannerView.OnPageClickCallback() {
                override fun onClick(view: View, position: Int) {
                    Snackbar.make(
                        bvBanner,
                        "position $position was clicked",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            setBannerViewHolder(SimpleBannerViewHolder(bannerItemArray))
        }

        btnRefresh.setOnClickListener {
            bannerItemArray.add(Color.YELLOW)
            bvBanner.notifyDataSetChanged()
        }

        rgIndicatorCustom.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.rbCustom0 -> {
                    bvBanner.setCustomIndicator(null)
                }
                R.id.rbCustom1 -> {
                    bvBanner.setCustomIndicator(FigureIndicatorView(this.context!!).also {
                        it.setPadding(dp6, dp6/2, dp6, dp6/2)
                    })
                }
            }
        }
        rgIndicatorVisibility.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb1 -> {
                    bvBanner.indicatorVisibility = View.GONE
                }
                R.id.rb0 -> {
                    bvBanner.indicatorVisibility = View.VISIBLE
                }
            }
        }

        rgIndicator.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb2 -> {
                    bvBanner.indicatorParentPosition = BannerIndicatorPosition.INSIDE
                }
                R.id.rb3 -> {
                    bvBanner.indicatorParentPosition = BannerIndicatorPosition.BELOW
                }
            }
        }

        rgIndicatorStyle.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {

                R.id.rbStyle1 -> {
                    bvBanner.setIndicatorOptions {
                        slideMode = IndicatorSlideMode.NORMAL
                    }
                }
                R.id.rbStyle2 -> {
                    bvBanner.setIndicatorOptions {
                        slideMode = IndicatorSlideMode.SMOOTH
                    }
                }
                R.id.rbStyle3 -> {
                    bvBanner.setIndicatorOptions {
                        slideMode = IndicatorSlideMode.WORM
                    }
                    outIndicator.refreshIndicatorOptions {  }
                }
            }
        }

        rgIndicatorShape.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbShape1 -> {
                    bvBanner.setIndicatorOptions {
                        indicatorStyle = IndicatorStyle.ROUND_RECT
                    }
                }
                R.id.rbShape2 -> {
                    bvBanner.setIndicatorOptions {
                        indicatorStyle = IndicatorStyle.DASH
                    }
                }
                R.id.rbShape3 -> {
                    bvBanner.setIndicatorOptions {
                        indicatorStyle = IndicatorStyle.CIRCLE
                    }
                }
            }
        }
    }
}