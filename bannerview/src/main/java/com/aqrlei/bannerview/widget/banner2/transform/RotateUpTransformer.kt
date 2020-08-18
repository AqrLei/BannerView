package com.aqrlei.bannerview.widget.banner2.transform

import android.view.View

private const val ROT_MOD = -15F

class RotateUpTransformer : BasePageTransformer() {

    private fun onTransform(page: View, position: Float) {
        val width = page.width
        val rotation = ROT_MOD * position

        page.pivotX = width * 0.5F
        page.pivotY = 0F
        page.translationX = 0F
        page.rotation = rotation
    }

    override fun onPreTransform(page: View, position: Float) {
        super.onPreTransform(page, position)
        onTransform(page, position)
    }

    override fun isPagingEnabled(): Boolean = true

}