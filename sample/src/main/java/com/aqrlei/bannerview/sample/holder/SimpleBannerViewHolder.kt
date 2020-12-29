package com.aqrlei.bannerview.sample.holder

import android.view.View
import android.widget.TextView
import com.aqrlei.bannerview.sample.R
import com.aqrlei.bannerview.widget.BannerViewHolder

/**
 * created by AqrLei on 2020/10/20
 */
class SimpleBannerViewHolder(private val list: ArrayList<Int>) : BannerViewHolder() {

    override fun getLayoutId(viewType: Int): Int = R.layout.layout_banner_item

    override fun getListSize(): Int = list.size

    override fun onBindView(position: Int, view: View) {
        val tvBanner = view.findViewById<TextView>(R.id.tvBanner)
        tvBanner.setBackgroundColor(list[position])
        tvBanner.text = "${position+1}"
    }
}