package com.aqrlei.bannerview.widget.indicator.base

/**
 * created by AqrLei on 2020/5/11
 */
interface OnPageChangeListener {

    fun onPageSelected(position: Int)

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

    fun onPageScrollStateChanged(state: Int)
}