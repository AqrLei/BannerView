package com.aqrlei.bannerview.widget.banner2.transform

import android.view.View

/**
 * created by AqrLei on 2020/4/23
 */
private const val MIN_SCALE = 0.75f

class DepthPageTransformer : BasePageTransformer() {

    override fun isPagingEnabled(): Boolean = true

    override fun onTransformMoveToLeft(page: View, position: Float) {
        super.onTransformMoveToLeft(page, position)
        with(page) {
            alpha = 1f
            translationX = 0f
            translationZ = 0f
            scaleX = 1f
            scaleY = 1f
        }
    }

    override fun onTransformMoveToRight(page: View, position: Float) {
        super.onTransformMoveToRight(page, position)
        with(page) {
            // Fade the page out.
            alpha = 1 - position

            // Counteract the default slide transition
            pivotY = 0.5F * height
            translationX = page.width * -position
            // Move it behind the left page
            translationZ = -1f

            // Scale the page down (between MIN_SCALE and 1)
            val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
            scaleX = scaleFactor
            scaleY = scaleFactor
        }
    }
}