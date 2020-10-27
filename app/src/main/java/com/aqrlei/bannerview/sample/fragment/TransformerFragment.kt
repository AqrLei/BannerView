package com.aqrlei.bannerview.sample.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.sample.R
import com.aqrlei.bannerview.sample.holder.SimpleBannerViewHolder
import com.aqrlei.bannerview.widget.BannerView
import com.aqrlei.bannerview.widget.enums.PageTransformerStyle
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_pager_transformer.*

/**
 * created by AqrLei on 2020/10/20
 */
class TransformerFragment : Fragment() {

    companion object {
        fun create() = TransformerFragment()
    }

    private val bannerItemArray = ArrayList<Int>().also {
        it.addAll(
            arrayOf(
                Color.GREEN,
                Color.MAGENTA,
                Color.BLUE,
                Color.CYAN,
                Color.RED,
                Color.YELLOW
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pager_transformer, container, false)
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

        rgPageStyle.setOnCheckedChangeListener { group, checkedId ->

            bvBanner.setBannerOptions {
                pageTransformerStyle = when (checkedId) {
                    R.id.rbStyle1 -> PageTransformerStyle.NORMAL
                    R.id.rbStyle2 -> PageTransformerStyle.MULTI_SCALE_IN
                    R.id.rbStyle3 -> PageTransformerStyle.MULTI_OVERLAP
                    else -> PageTransformerStyle.NORMAL
                }
            }

        }
        rgPageOrientation.setOnCheckedChangeListener { group, checkedId ->
            bvBanner.orientation = if (checkedId == R.id.rb1) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.VISIBLE
        }

    }
}