package com.aqrlei.bannerview.widget.transform

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * created by AqrLei on 2020/4/24
 */
abstract class BasePageTransformer :
    ViewPager2.PageTransformer {
    final override fun transformPage(page: View, position: Float) {
        onPreTransform(page, position)
        when {
            position < -1 -> onTransformOffScreenLeft(page, position)
            position <= 0 -> onTransformMoveToLeft(page, position)
            position <= 1 -> onTransformMoveToRight(page, position)
            else -> onTransformOffScreenRight(page, position)
        }
    }

    protected open fun onPreTransform(page: View, position: Float) {}
    protected open fun onTransformOffScreenLeft(page: View, position: Float) {}
    protected open fun onTransformMoveToLeft(page: View, position: Float) {}
    protected open fun onTransformMoveToRight(page: View, position: Float) {}
    protected open fun onTransformOffScreenRight(page: View, position: Float) {}
}