package com.aqrlei.bannerview.widget.transform

import android.view.View
import com.aqrlei.bannerview.widget.options.MIN_SCALE

private const val MIN_ALPHA = 0.5f

class ZoomOutPageTransformer : BasePageTransformer() {

    override fun onPreTransform(page: View, position: Float) {
        with(page) {
            rotationX = 0F
            rotationY = 0F
            rotation = 0F
            scaleX = 1F
            scaleY = 1F
            pivotX = 0F
            pivotY = 0F
            translationY = 0F
            translationX = -width * position
            page.alpha = if ((position <= -1f || position >= 1f)) 0F else 1f
        }
    }

    override fun onTransformMoveToLeft(page: View, position: Float) {
        onTransform(page, position)
    }

    override fun onTransformMoveToRight(page: View, position: Float) {
        onTransform(page, position)
    }

    private fun onTransform(page: View, position: Float) {
        with(page) {
            // Modify the default slide transition to shrink the page as well
            val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
            val vertMargin = height * (1 - scaleFactor) / 2
            val horzMargin = width * (1 - scaleFactor) / 2
            translationX = if (position < 0) {
                horzMargin - vertMargin / 2
            } else {
                horzMargin + vertMargin / 2
            }

            // Scale the page down (between MIN_SCALE and 1)
            scaleX = scaleFactor
            scaleY = scaleFactor

            // Fade the page relative to its size.
            alpha = (MIN_ALPHA +
                    (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
        }
    }
}