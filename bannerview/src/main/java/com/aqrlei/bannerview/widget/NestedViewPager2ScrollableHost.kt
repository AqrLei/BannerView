/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aqrlei.bannerview.widget;

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.math.tan

/**
 * Layout to wrap a scrollable component inside a ViewPager2. Provided as a solution to the problem
 * where pages of ViewPager2 have nested scrollable elements that scroll in the same direction as
 * ViewPager2. The scrollable element needs to be the immediate and only child of this host layout.
 *
 * This solution has limitations when using multiple levels of nested scrollable elements
 * (e.g. a horizontal RecyclerView in a vertical RecyclerView in a horizontal ViewPager2).
 */
class NestedViewPager2ScrollableHost : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var touchSlop = 0
    private var initialX = 0f
    private var initialY = 0f

    private val child: ViewPager2? get() = if (childCount > 0) getChildAt(0) as? ViewPager2 else null

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            0 -> child?.canScrollHorizontally(direction) ?: false
            1 -> child?.canScrollVertically(direction) ?: false
            else -> throw IllegalArgumentException()
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        child ?: return

        // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(child!!.orientation, -1f) && !canChildScroll(child!!.orientation, 1f)) {
            return
        }

        when(e.action){
            MotionEvent.ACTION_DOWN -> {
                initialX = e.x
                initialY = e.y
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - initialX
                val dy = e.y - initialY
                val absDx = dx.absoluteValue
                val absDy = dy.absoluteValue
                // x/y 滑过的距离绝对值 比大于tan30时 才归类为横向滑动
                val isSlideHorizontal = absDx > absDy
                val isVpHorizontal = child!!.orientation == ORIENTATION_HORIZONTAL

                when{
                    isVpHorizontal && isSlideHorizontal && absDx > touchSlop -> {
                        parent.requestDisallowInterceptTouchEvent(
                            canChildScroll(child!!.orientation, dx)
                        )
                    }

                    !isVpHorizontal && !isSlideHorizontal && absDy > touchSlop -> {
                        parent.requestDisallowInterceptTouchEvent(
                            canChildScroll(child!!.orientation, dy)
                        )
                    }

                    else -> {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }

            }
        }
    }
}