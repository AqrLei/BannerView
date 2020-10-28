package com.aqrlei.bannerview.sample.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.sample.R
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorStyle
import kotlinx.android.synthetic.main.fragment_pager_home.*
import kotlinx.android.synthetic.main.layout_banner_item.view.*

/**
 * created by AqrLei on 2020/10/20
 */
class HomeFragment : Fragment() {
    companion object {
        fun create() = HomeFragment()
    }

    private val bannerItemArray = ArrayList<Int>().also {
        it.addAll(arrayOf(Color.MAGENTA, Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pager_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dp3 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            3F,
            this.context!!.resources.displayMetrics
        )
        viewPager2.adapter = object : RecyclerView.Adapter<ItemViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                return ItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_banner_item, parent, false)
                )
            }

            override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
                holder.itemView.tvBanner.setBackgroundColor(bannerItemArray[position])
                holder.itemView.tvBanner.text = "${position+1}"
            }

            override fun getItemCount(): Int = bannerItemArray.size
        }
        indicatorBottom.setupWithViewPager2(viewPager2) {
            indicatorStyle = IndicatorStyle.ROUND_RECT
            normalIndicatorWidth = dp3 * 2
            checkedIndicatorWidth = dp3 * 4
            indicatorGap = dp3.toInt()*2
            sliderHeight = dp3 * 2
            checkedColor = Color.GREEN
        }
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}