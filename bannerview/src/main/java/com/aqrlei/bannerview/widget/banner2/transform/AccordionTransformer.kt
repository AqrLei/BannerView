package com.aqrlei.bannerview.widget.banner2.transform

import android.view.View

/**
 * created by AqrLei on 2020/4/24
 */
class AccordionTransformer :BasePageTransformer(){
     private fun onTransform(page: View, position: Float) {
        page.pivotX = if (position < 0F) 0F else page.width.toFloat()
        page.scaleX = if(position < 0F) (1f+position) else (1F- position)
    }

    override fun onPreTransform(page: View, position: Float) {
        super.onPreTransform(page, position)
        onTransform(page, position)
    }
}