package com.aqrlei.bannerview.widget.banner2.transform

import android.view.View

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

class ZoomOutPageTransformer : BasePageTransformer() {

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

    override fun onTransformMoveToLeft(page: View, position: Float) {
        super.onTransformMoveToLeft(page, position)
        onTransform(page, position)
    }

    override fun onTransformMoveToRight(page: View, position: Float) {
        super.onTransformMoveToRight(page, position)
        onTransform(page, position)
    }
}