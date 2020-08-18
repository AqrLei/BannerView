package com.aqrlei.bannerview.widget.banner2.transform.factory

import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.enums.TransformerStyle
import com.aqrlei.bannerview.widget.banner2.transform.*

/**
 * created by AqrLei on 2020/4/23
 */
private const val MAX_SCALE = 0.999F

object BannerPageTransformerFactory {

    fun createPageTransformer(
        type: TransformerStyle,
        transformerScale: Float = 0.85f): ViewPager2.PageTransformer {
        return when (type) {
            TransformerStyle.NONE -> ScaleInTransformer(MAX_SCALE)
            TransformerStyle.SCALE_IN -> ScaleInTransformer(transformerScale)
            TransformerStyle.DEPTH -> DepthPageTransformer()
            TransformerStyle.STACK -> ScaleInTransformer(MAX_SCALE)
            TransformerStyle.ACCORDION -> AccordionTransformer()
            TransformerStyle.ROTATE -> RotateUpTransformer()
            TransformerStyle.ZOOM_OUT -> ZoomOutPageTransformer()
            TransformerStyle.MULTI -> ScaleInTransformer(transformerScale)
            TransformerStyle.MULTI_OVERLAP -> ScaleInOverlapTransformer(transformerScale)
        }
    }
}