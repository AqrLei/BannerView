package com.aqrlei.bannerview.widget.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

abstract class ViewHolder {
        @LayoutRes
        abstract fun getLayoutId(viewType: Int): Int
        abstract fun getItemCount(): Int
        fun createBannerView(parent: ViewGroup, viewType: Int): View {
            val view = LayoutInflater.from(parent.context).inflate(getLayoutId(viewType), parent, false)
            return onViewCreate(view, parent)
        }

        protected open fun onViewCreate(view: View, parent: ViewGroup): View {
            return view
        }

        open fun getItemViewType(position: Int): Int? {
            return null
        }

        abstract fun bindView(position: Int, view: View)
    }