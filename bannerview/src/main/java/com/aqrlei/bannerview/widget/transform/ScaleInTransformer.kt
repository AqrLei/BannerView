package com.aqrlei.bannerview.widget.transform

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.options.MIN_SCALE

/**
 * created by AqrLei on 2020/4/24
 */
private const val CENTER_SCALE = 0.5F

class ScaleInTransformer(private val minScale: Float = MIN_SCALE) : BasePageTransformer() {
    private var isHorizontal: Boolean = true

    override fun onPreTransform(page: View, position: Float) {
        isHorizontal = isHorizontal(page)
        with(page) {
            pivotY = height / 2F
            pivotX = width / 2F
        }
    }

    override fun onTransformOffScreenLeft(page: View, position: Float) {
        with(page) {
            scaleX = minScale
            scaleY = minScale
            if (isHorizontal) {
                pivotX = width.toFloat()
            } else {
                pivotY = height.toFloat()
            }
        }
    }

    override fun onTransformMoveToLeft(page: View, position: Float) {
        with(page) {
            val scaleFactor: Float = (1 + position) * (1 - minScale) + minScale
            scaleX = scaleFactor
            scaleY = scaleFactor

            if (isHorizontal) {
                pivotX = width * (CENTER_SCALE + CENTER_SCALE * -position)
            } else {
                pivotY = height * (CENTER_SCALE + CENTER_SCALE * -position)
            }
        }
    }

    override fun onTransformMoveToRight(page: View, position: Float) {
        with(page) {
            val scaleFactor: Float = (1 - position) * (1 - minScale) + minScale
            scaleX = scaleFactor
            scaleY = scaleFactor
            if (isHorizontal) {
                pivotX = width * ((1 - position) * CENTER_SCALE)
            } else {
                pivotY = height * ((1 - position) * CENTER_SCALE)
            }
        }
    }

    override fun onTransformOffScreenRight(page: View, position: Float) {
        with(page) {
            pivotX = 0F
            pivotY = 0F
            scaleX = minScale
            scaleY = minScale
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