package com.aqrlei.bannerview.widget.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.BannerView
import com.aqrlei.bannerview.widget.utils.BannerUtils
import com.aqrlei.bannerview.widget.viewholder.ViewHolder

class Adapter<VH : ViewHolder>(val viewHolder: VH) : ViewPager2.OnPageChangeCallback() {
        
        companion object {
            internal const val LOOP_ORIGIN_INDEX = 1
        }

        var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null
        var pageClickCallback: OnPageClickCallback? = null

        var currentPosition: Int = 0
            internal set

        private var currentLoopPosition: Int = 0

        var bannerView: BannerView? = null
            internal set

        internal val bannerPagerAdapter: BannerPagerAdapter by lazy { BannerPagerAdapter() }

        fun notifyDataChange() {
            // 刷新的时候 回到第一个位置
            currentPosition = 0

            // 如果是可循环的，改变currentLoopPosition的值
            setToLoopOriginIndex()
            bannerView?.bannerManager?.bannerOptions?.indicatorOptions {
                currentPosition = this@Adapter.currentPosition
            }
            bannerView?.refreshBanner()
        }

        fun getRealItemCount(): Int = viewHolder.getItemCount()

        internal fun getAutoNextPosition(curIndex: Int): Int {
            return curIndex + 1
        }

        private fun setToLoopOriginIndex() {
            if (getRealItemCount() > 0 && bannerView!!.isCanLoop) {
                currentLoopPosition = loopOriginIndex()
            }
        }

        internal fun getLoopItemIndexByOffset(offset: Int) =
            BannerUtils.getLoopIndex((loopOriginIndex() + offset), getRealItemCount())

        internal fun getOriginBeginIndex(): Int {
            setToLoopOriginIndex()
            return if (getRealItemCount() > 1 && bannerView!!.isCanLoop) currentLoopPosition else currentPosition
        }

        override fun onPageSelected(position: Int) {
            bannerView ?: return
            bannerView!!.logDebug("onPageSelected: position - $position")
            val size = getRealItemCount()
            currentPosition = BannerUtils.getRealPosition2(bannerView!!.isCanLoop, position, size)
            currentLoopPosition = position

            bannerView!!.logDebug("onPageSelected: currentPosition-$currentPosition , curLoopPosition : $currentLoopPosition")
            bannerView!!.indicatorView?.onPageSelected(currentPosition)
            pageChangeCallback?.onPageSelected(currentPosition)
        }

        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager2.SCROLL_STATE_SETTLING, ViewPager2.SCROLL_STATE_DRAGGING -> {
                }
                ViewPager2.SCROLL_STATE_IDLE -> {
                    if (getRealItemCount() > 1 && bannerView!!.isCanLoop) {
                        val curIndex = bannerView!!.viewPager2.currentItem
                        if (curIndex == 0) {
                            bannerView!!.setCurrentItem(getRealItemCount() - 1, false)
                        } else if (curIndex == getItemCount() - 1) {
                            bannerView!!.setCurrentItem(0, false)
                        }
                    }
                }
            }
            bannerView?.indicatorView?.onPageScrollStateChanged(state)
            pageChangeCallback?.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int) {
            bannerView ?: return
            val realItemCount = getRealItemCount()
            if (realItemCount > 0) {
                val realPosition =
                    BannerUtils.getRealPosition2(bannerView!!.isCanLoop, position, realItemCount)
                bannerView?.indicatorView?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels)
                bannerView?.logDebug("onPageScrolled: realPosition-$realPosition")
                pageChangeCallback?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels)
            }
        }

        /**
         * 可循环时的初始位置
         */
        private fun loopOriginIndex() =
            LOOP_ORIGIN_INDEX

        private fun getItemCount() = bannerPagerAdapter.itemCount

        inner class BannerPagerAdapter :
            RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int): RecyclerView.ViewHolder {
                return BannerViewHolder(viewHolder.createBannerView(parent,viewType))
            }

            override fun getItemViewType(position: Int): Int {
                val realPosition = BannerUtils.getRealPosition2(bannerView!!.isCanLoop,position,getRealItemCount())
                return viewHolder.getItemViewType(realPosition) ?: super.getItemViewType(position)
            }

            override fun getItemCount(): Int {
                return if (bannerView!!.isCanLoop && viewHolder.getItemCount() > 1) viewHolder.getItemCount() + 2 else viewHolder.getItemCount()
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (itemCount > 0) {
                    val realPosition =
                        BannerUtils.getRealPosition2(
                            bannerView!!.isCanLoop,
                            position,
                            this@Adapter.getRealItemCount())
                    val view = holder.itemView.also { v ->
                        v.setOnClickListener {
                            pageClickCallback?.onClick(v, realPosition)
                        }
                    }
                    bannerView!!.logDebug("onBindViewHolder - position : $position")
                    bannerView!!.logDebug("onBindViewHolder - realPosition : $realPosition")
                    bannerView!!.logDebug("onBindViewHolder - itemCount : $itemCount")
                    viewHolder.bindView(realPosition, view)
                }
            }

            override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                super.onAttachedToRecyclerView(recyclerView)
                bannerView?.viewPager2?.registerOnPageChangeCallback(this@Adapter)
            }

            override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
                bannerView?.viewPager2?.unregisterOnPageChangeCallback(this@Adapter)
                super.onDetachedFromRecyclerView(recyclerView)
            }
        }

        open class OnPageClickCallback {
            open fun onClick(view: View, position: Int) {}
        }

        inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }