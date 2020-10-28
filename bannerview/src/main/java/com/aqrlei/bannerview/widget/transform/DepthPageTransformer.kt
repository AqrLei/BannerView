package com.aqrlei.bannerview.widget.transform

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.aqrlei.bannerview.widget.options.MIN_SCALE

/**
 * created by AqrLei on 2020/4/23
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class DepthPageTransformer : BasePageTransformer() {

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
            translationX = 0F
            page.alpha = if ((position <= -1f || position >= 1f)) 0F else 1f
        }
    }

    override fun onTransformMoveToLeft(page: View, position: Float) {
        with(page) {
            alpha = 1f
            if(isHorizontal){
                translationX = 0F
            }else {
                translationY = 0F
            }

            translationZ = 0f
            scaleX = 1F
            scaleY = 1F
        }
    }

    override fun onTransformMoveToRight(page: View, position: Float) {
        with(page) {
            // Fade the page out.
            alpha = 1 - position

            // Counteract the default slide transition
            if (isHorizontal) {
                pivotY = 0.5F * height
                translationX = page.width * -position
            }else {
                pivotX = 0.5f * width
                translationY = page.height * -position
            }
            // Move it behind the left page
            translationZ = -1f

            // Scale the page down (between MIN_SCALE and 1)
            val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
            scaleX = scaleFactor
            scaleY = scaleFactor
        }
    }
}