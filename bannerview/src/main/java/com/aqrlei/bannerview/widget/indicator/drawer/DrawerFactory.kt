package com.aqrlei.bannerview.widget.indicator.drawer

import com.aqrlei.bannerview.widget.enums.IndicatorStyle
import com.aqrlei.bannerview.widget.options.IndicatorOptions

/**
 * created by AqrLei on 2019-12-11
 */
object DrawerFactory {
    fun createDrawer(indicatorOptions: IndicatorOptions): IDrawer {
        return when (indicatorOptions.indicatorStyle) {
            IndicatorStyle.DASH -> DashDrawer(indicatorOptions)
            IndicatorStyle.CIRCLE -> CircleDrawer(indicatorOptions)
            IndicatorStyle.ROUND_RECT -> RoundRectDrawer(indicatorOptions)
        }
    }
}