package com.aqrlei.bannerview.widget.transform

import android.view.View

/**
 * created by AqrLei on 2020/4/24
 */
class AccordionTransformer : BasePageTransformer() {

    override fun onPreTransform(page: View, position: Float) {
        with(page) {
            rotationX = 0F
            rotationY = 0F
            rotation = 0F
            scaleX = 1F
            scaleY = 1F
            pivotX = 0F
            pivotY = 0F
            if (isHorizontal) {
                translationY = 0F
                translationX = -width * position
            }else {
                translationX = 0F
                translationY = -height * position
            }
            page.alpha = if ((position <= -1f || position >= 1f)) 0F else 1f
        }
        onTransform(page, position)
    }

    private fun onTransform(page: View, position: Float) {
        if(isHorizontal) {
            page.pivotX = if (position < 0F) 0F else page.width.toFloat()
            page.scaleX = if (position < 0F) (1f + position) else (1F - position)
        } else {
            page.pivotY = if (position < 0F) 0F else page.height.toFloat()
            page.scaleY = if (position < 0F) (1f + position) else (1F - position)
        }
    }
}