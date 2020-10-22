package com.aqrlei.bannerview.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * created by AqrLei on 2020/10/22
 */
abstract class BannerViewHolder {

    open fun getItemViewType(position: Int): Int? {
        return null
    }

    @LayoutRes
    abstract fun getLayoutId(viewType: Int): Int

    abstract fun getListSize(): Int

    fun onCreateBannerView(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(parent.context).inflate(getLayoutId(viewType), parent, false)
    }

    abstract fun onBindView(position: Int, view: View)

}