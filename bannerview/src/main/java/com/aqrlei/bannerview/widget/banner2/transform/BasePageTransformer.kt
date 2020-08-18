package com.aqrlei.bannerview.widget.banner2.transform

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

    protected open fun isPagingEnabled() = false
    protected open fun hideOffScreenPages() = true
    protected open fun onPreTransform(page: View, position: Float) {
        with(page) {
            rotationX = 0F
            rotationY = 0F
            rotation = 0F
            scaleX = 1F
            scaleY = 1F
            pivotX = 0F
            pivotY = 0F
            translationY = 0F
            translationX = if (isPagingEnabled()) 0F else -width * position
            page.alpha = if (hideOffScreenPages() && (position <= -1f || position >= 1f)) {
                0F
            } else {
                1f
            }
        }
    }

    protected open fun onTransformOffScreenLeft(page: View, position: Float) {}
    protected open fun onTransformOffScreenRight(page: View, position: Float) {}
    protected open fun onTransformMoveToLeft(page: View, position: Float) {}
    protected open fun onTransformMoveToRight(page: View, position: Float) {}


}