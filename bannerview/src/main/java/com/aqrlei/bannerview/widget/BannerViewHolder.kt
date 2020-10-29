package com.aqrlei.bannerview.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil

/**
 * created by AqrLei on 2020/10/22
 */
abstract class BannerViewHolder {

    protected var bannerView: BannerView? = null

    /**
     * 数据变化通知刷新
     */
    fun notifyDataSetChanged(diffCallback: DiffUtil.Callback? = null) {
        bannerView?.notifyDataSetChanged(diffCallback)
    }

    @CallSuper
    open fun onAttachedToBannerView(bannerView: BannerView) {
        this.bannerView = bannerView
    }

    @CallSuper
    open fun onDetachedFromBannerView(bannerView: BannerView) {
        this.bannerView = null
    }

    open fun getItemViewType(position: Int): Int? {
        return null
    }

    @LayoutRes
    abstract fun getLayoutId(viewType: Int): Int

    abstract fun getListSize(): Int

    fun onCreateBannerView(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(parent.context).inflate(getLayoutId(viewType), parent, false).also {
            // avoid ERROR: Pages must fill the whole ViewPager2 (use match_parent)
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    abstract fun onBindView(position: Int, view: View)

}