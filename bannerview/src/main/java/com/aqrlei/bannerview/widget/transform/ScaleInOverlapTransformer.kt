package com.aqrlei.bannerview.widget.transform

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

/**
 * created by AqrLei on 2020/4/24
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ScaleInOverlapTransformer(private val minScale: Float = 0.85F) : BasePageTransformer() {
    override fun onPreTransform(page: View, position: Float) {
        val isHorizontal = isHorizontal(page)
        with(page) {
            elevation = -abs(position)
            val scaleDelta = abs(position * 0.2F)
            val scale = max(1f - scaleDelta, minScale)
            scaleX = scale
            scaleY = scale
            if (isHorizontal) {
                translationX = if (position > 0) (-width * (1f - scale)) else (width * (1f - scale))
            } else {
                translationY = if (position > 0) (-width * (1f - scale)) else (width * (1f - scale))
            }
        }
    }

    private fun isHorizontal(page: View): Boolean {
        val parent = page.parent
        val parentParent = parent?.parent
        return if (parent is RecyclerView && parentParent is ViewPager2) {
            parentParent.orientation == ViewPager2.ORIENTATION_HORIZONTAL
        } else true
    }
}