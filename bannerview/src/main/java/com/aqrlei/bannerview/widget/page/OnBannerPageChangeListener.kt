package com.aqrlei.bannerview.widget.page

/**
 * created by AqrLei on 2020/5/11
 */
interface OnBannerPageChangeListener {

    fun onPageSelected(position: Int)

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

    fun onPageScrollStateChanged(state: Int)
}