package com.aqrlei.bannerview.sample.holder

import android.view.View
import com.aqrlei.bannerview.sample.R
import com.aqrlei.bannerview.widget.BannerView
import com.aqrlei.bannerview.widget.viewholder.ViewHolder
import kotlinx.android.synthetic.main.layout_banner_item.view.*

/**
 * created by AqrLei on 2020/10/20
 */
class SimpleBannerViewHolder(private val list: Array<Int>) : ViewHolder() {

    override fun getLayoutId(viewType: Int): Int = R.layout.layout_banner_item

    override fun getItemCount(): Int = list.size

    override fun bindView(position: Int, view: View) {
        view.tvBanner.setBackgroundColor(list[position])
        view.tvBanner.text = "$position"
    }
}