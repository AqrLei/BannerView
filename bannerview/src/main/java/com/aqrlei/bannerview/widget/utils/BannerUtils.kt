package com.aqrlei.bannerview.widget.utils

import android.content.res.Resources

/**
 * created by AqrLei on 2019-12-11
 */
object BannerUtils {

    fun dp2px(dpValue: Float) = (0.5F + dpValue * Resources.getSystem().displayMetrics.density)

    fun getRealPosition2(isCanLoop: Boolean, position: Int, pageSize: Int): Int {
        return if (pageSize == 0) 0 else (position + pageSize) % pageSize
    }

    fun getLoopIndex(position: Int, pageSize: Int): Int {
        return when {
            position <= 0 -> 1
            position >= pageSize -> pageSize
            else -> position
        }
    }
}