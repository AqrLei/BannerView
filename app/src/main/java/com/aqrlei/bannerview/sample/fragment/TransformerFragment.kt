package com.aqrlei.bannerview.sample.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.sample.R
import com.aqrlei.bannerview.sample.holder.SimpleBannerViewHolder
import com.aqrlei.bannerview.widget.BannerView
import com.aqrlei.bannerview.widget.enums.PageTransformerStyle
import com.aqrlei.bannerview.widget.transform.AccordionTransformer
import com.aqrlei.bannerview.widget.transform.DepthPageTransformer
import com.aqrlei.bannerview.widget.transform.ZoomOutPageTransformer
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
        val dp5 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            5F,
            this.resources.displayMetrics
        )
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

        rgPageStyle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbStyle1 -> {
                    bvBanner.setBannerOptions {
                        pageTransformerStyle = PageTransformerStyle.NORMAL
                        startRevealWidth = 0F
                        endRevealWidth = 0F
                        pageMargin = 0F
                    }
                }
                R.id.rbStyle2 -> {
                    bvBanner.setBannerOptions {
                        pageTransformerStyle = PageTransformerStyle.MULTI_SCALE_IN

                        startRevealWidth = dp5 * 6
                        endRevealWidth = dp5 * 6
                        pageMargin = dp5 * 2
                    }
                }
                R.id.rbStyle3 -> {
                    bvBanner.setBannerOptions {
                        pageTransformerStyle = PageTransformerStyle.MULTI_OVERLAP
                        startRevealWidth = dp5 * 6
                        endRevealWidth = dp5 * 6
                        pageMargin = 0F
                    }
                }

                R.id.rbStyle4 -> {
                    bvBanner.setBannerOptions {
                        pageTransformerStyle = PageTransformerStyle.NORMAL
                        startRevealWidth = dp5 * 6
                        endRevealWidth = dp5 * 6
                        pageMargin = dp5 * 2
                    }
                }

                R.id.rbStyle5 -> {
                    bvBanner.removeDefaultPageTransformer()
                    bvBanner.setBannerOptions {
                        pageTransformerStyle = PageTransformerStyle.NORMAL
                        startRevealWidth = 0F
                        endRevealWidth = dp5 * 6
                        pageMargin = dp5 * 2
                    }

                }
                R.id.rbStyle6 -> {
                    bvBanner.setBannerOptions {
                        pageTransformerStyle = PageTransformerStyle.NORMAL
                        startRevealWidth = 0F
                        endRevealWidth = 0F
                        pageMargin = dp5 * 2
                    }
                }
            }
        }
        rgPageOrientation.setOnCheckedChangeListener { _, checkedId ->
            bvBanner.setBannerOptions {
                orientation =
                    if (checkedId == R.id.rb1) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
            }
        }

        val accordion = AccordionTransformer()
        val depth = DepthPageTransformer()
        val zoomOut  = ZoomOutPageTransformer()
        rgTransformerStyle.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.rbTStyle0 -> {
                    bvBanner.removePageTransformer(depth)
                    bvBanner.removePageTransformer(zoomOut)
                    bvBanner.removePageTransformer(accordion)
                }
                R.id.rbTStyle1 -> {
                    bvBanner.removePageTransformer(depth)
                    bvBanner.removePageTransformer(zoomOut)
                    bvBanner.removePageTransformer(accordion)
                    bvBanner.addPageTransformer(accordion)
                }
                R.id.rbTStyle2 -> {
                    bvBanner.removePageTransformer(depth)
                    bvBanner.removePageTransformer(zoomOut)
                    bvBanner.removePageTransformer(accordion)
                    bvBanner.addPageTransformer(depth)
                }
                R.id.rbTStyle3 -> {
                    bvBanner.removePageTransformer(depth)
                    bvBanner.removePageTransformer(zoomOut)
                    bvBanner.removePageTransformer(accordion)
                    bvBanner.addPageTransformer(zoomOut)
                }
            }
        }
    }
}