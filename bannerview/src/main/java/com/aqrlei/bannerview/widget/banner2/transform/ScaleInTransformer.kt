package com.aqrlei.bannerview.widget.banner2.transform

import android.view.View

/**
 * created by AqrLei on 2020/4/24
 */
private const val MIN_SCALE = 0.85F
private const val CENTER_SCALE = 0.5F
class ScaleInTransformer(private val minScale: Float = MIN_SCALE) : BasePageTransformer() {
    override fun onPreTransform(page: View, position: Float) {
        with(page) {
            pivotY = height / 2F
            pivotX = width / 2F
        }
    }

    override fun onTransformOffScreenLeft(page: View, position: Float) {
        with(page) {
            scaleX = minScale
            scaleY = minScale
            pivotX = width.toFloat()
        }
    }

    override fun onTransformOffScreenRight(page: View, position: Float) {
        with(page) {
            pivotX = 0f
            scaleX = minScale
            scaleY = minScale
        }
    }

    override fun onTransformMoveToLeft(page: View, position: Float) {
        super.onTransformMoveToLeft(page, position)
        with(page) {
            val scaleFactor: Float = (1 + position) * (1 - minScale) + minScale
            scaleX = scaleFactor
            scaleY = scaleFactor
            pivotX = width * (CENTER_SCALE + CENTER_SCALE * -position)
        }
    }

    override fun onTransformMoveToRight(page: View, position: Float) {
        super.onTransformMoveToRight(page, position)
        with(page) {
            val scaleFactor: Float = (1 - position) * (1 - minScale) + minScale
            page.scaleX = scaleFactor
            page.scaleY = scaleFactor
            page.pivotX = width * ((1 - position) * CENTER_SCALE)
        }
    }
}