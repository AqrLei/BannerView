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
import com.aqrlei.bannerview.widget.adapter.Adapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_pager_indicator.*

/**
 * created by AqrLei on 2020/10/20
 */
class IndicatorFragment : Fragment() {
    companion object {
        fun create() = IndicatorFragment()
    }


    private val bannerItemArray =
        arrayOf(Color.GREEN, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.RED)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pager_indicator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bvBanner.adapter =
           Adapter(SimpleBannerViewHolder(bannerItemArray)).also { adapter ->
                adapter.pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        Log.d(BannerView.TAG, "position $position was selected")
                    }
                }
                adapter.pageClickCallback = object : Adapter.OnPageClickCallback() {
                    override fun onClick(view: View, position: Int) {
                        Snackbar.make(
                            bvBanner,
                            "position $position was clicked",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

            }
    }
}