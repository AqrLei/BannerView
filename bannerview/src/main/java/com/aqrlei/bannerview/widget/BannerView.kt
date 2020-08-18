package com.aqrlei.bannerview.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aqrlei.bannerview.widget.BuildConfig.logger
import com.aqrlei.bannerview.widget.enums.IndicatorGravity
import com.aqrlei.bannerview.widget.enums.IndicatorPosition
import com.aqrlei.bannerview.widget.indicator.IIndicator
import com.aqrlei.bannerview.widget.indicator.IndicatorView
import com.aqrlei.bannerview.widget.manager.BannerManager
import com.aqrlei.bannerview.widget.options.DEFAULT_SCROLL_DURATION
import com.aqrlei.bannerview.widget.utils.BannerUtils

/**
 * created by AqrLei on 2019-12-11
 */
class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "BannerView"
        private var logEnable = true
        fun logEnable(enable: Boolean) {
            logEnable = enable
        }
    }

    var indicatorView: IIndicator? = null

    private val indicatorLayout: ConstraintLayout = ConstraintLayout(context)
    private val viewPager: BannerViewPager =
        BannerViewPager(context)
    private val bannerManager: BannerManager = BannerManager()

    var adapter: Adapter<*>? = null
        set(value) {
            field = value
            field?.bannerView = this
            initBanner()
            logDebug("viewPagerHeight : ${viewPager.height}, viewPagerWidth : ${viewPager.width}")
        }

    private fun setLooping(loop: Boolean) {
        bannerManager.bannerOptions.isLooping = loop
    }

    private fun interval(): Long = bannerManager.bannerOptions.interval.toLong()
    private fun isAutoPlay() = bannerManager.bannerOptions.isAutoPlay
    private fun isLooping() = bannerManager.bannerOptions.isLooping
    private fun isCanLoop() = bannerManager.bannerOptions.isCanLoop

    private val runnable = Runnable { handlePosition() }

    init {
        bannerManager.initAttrs(context, attrs)
        viewPager.id = R.id.bvBanner
        indicatorLayout.id = R.id.bvIndicator
    }

    fun setCurrentItem(item: Int) {
        adapter?.let {
            viewPager.currentItem = if (isCanLoop() && it.getRealItemCount() > 1)
                getLoopItemIndexByOffset(item)
            else item
        }
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        adapter?.let {
            val index =
                if (isCanLoop() && it.getRealItemCount() > 1)
                    getLoopItemIndexByOffset(item)
                else item

            logDebug("setCurrentItem: curIndex - ${viewPager.currentItem} loopIndex: $index")
            viewPager.setCurrentItem(index, smoothScroll)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startLoop()
    }

    override fun onDetachedFromWindow() {
        stopLoop()
        super.onDetachedFromWindow()
    }

    /**
     * 点击控件的时候，轮播停止
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                setLooping(true)
                stopLoop()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_OUTSIDE -> {
                setLooping(false)
                startLoop()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun startLoop() {
        adapter?.let {
            if (!isLooping() && isAutoPlay() && it.getRealItemCount() > 1) {
                postDelayed(runnable, interval())
                setLooping(true)
            }
        }
    }

    private fun stopLoop() {
        if (isLooping()) {
            removeCallbacks(runnable)
            setLooping(false)
        }
    }

    private fun initBanner() {
        removeAllViews()
        setIndicatorValues()
        setupViewPager()
        addBannerChildView()
    }

    private fun refreshBanner() {
        stopLoop()
        setIndicatorValues()
        setupViewPager()
    }

    private fun addBannerChildView() {
        // 添加banner容器ViewPager
        this.addView(viewPager, LayoutParams(0, 0).apply {
            startToStart = LayoutParams.PARENT_ID
            topToTop = LayoutParams.PARENT_ID
            endToEnd = LayoutParams.PARENT_ID
            if (bannerManager.bannerOptions.indicatorPosition == IndicatorPosition.BELOW) {
                bottomToTop = indicatorLayout.id
                verticalChainStyle = LayoutParams.CHAIN_PACKED
            } else {
                bottomToBottom = LayoutParams.PARENT_ID
            }
        })

        // 添加指示器容器布局
        this.addView(indicatorLayout.also { it.background = ColorDrawable(Color.TRANSPARENT) },
            LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
                if (bannerManager.bannerOptions.indicatorPosition == IndicatorPosition.BELOW) {
                    topToBottom = viewPager.id
                } else {
                    topToTop = LayoutParams.PARENT_ID
                    verticalBias = bannerManager.bannerOptions.indicatorVerticalBias
                }
                bottomToBottom = LayoutParams.PARENT_ID
            })

        val bannerRatio = bannerManager.bannerOptions.widthHeightRatio

        bannerRatio.takeIf { it.isNotBlank() && bannerManager.bannerOptions.bannerUseRatio }?.let {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            constraintSet.setDimensionRatio(viewPager.id, bannerRatio)
            constraintSet.applyTo(this)
        }
    }

    private fun handlePosition() {
        adapter?.let {
            if (it.getRealItemCount() > 1) {
                val curIndex = viewPager.currentItem
                viewPager.currentItem = it.getAutoNextPosition(curIndex)
                postDelayed(runnable, interval())
            }
        }
    }

    private fun setIndicatorValues() {
        adapter?.let {
            val bannerOptions = bannerManager.bannerOptions
            if (null == indicatorView) {
                indicatorView = IndicatorView(context)
            }
            bannerManager.bannerOptions.indicatorOptions {
                customIndicator = indicatorView !is IndicatorView
            }
            indicatorView?.indicatorOptions = bannerOptions.indicatorOptions
            indicatorView?.setPageSize(it.getRealItemCount())
            initIndicator()
        }
    }

    private fun initIndicator() {
        indicatorLayout.visibility = bannerManager.bannerOptions.indicatorVisibility
        (indicatorView as? View)?.let {
            if (null == it.parent) {
                indicatorLayout.removeAllViews()
                it.layoutParams
                indicatorLayout.addView(it,
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also { lp ->
                        lp.startToStart = LayoutParams.PARENT_ID
                        lp.topToTop = LayoutParams.PARENT_ID
                        lp.endToEnd = LayoutParams.PARENT_ID
                        lp.bottomToBottom = LayoutParams.PARENT_ID

                        val indicatorMargin = bannerManager.bannerOptions.indicatorMargin

                        lp.marginStart = indicatorMargin.start
                        lp.topMargin = indicatorMargin.top
                        lp.marginEnd = indicatorMargin.end
                        lp.bottomMargin = indicatorMargin.bottom
                        lp.horizontalBias = when (bannerManager.bannerOptions.indicatorGravity) {
                            IndicatorGravity.START -> 0F
                            IndicatorGravity.CENTER -> 0.5F
                            IndicatorGravity.END -> 1.0F
                            IndicatorGravity.BIAS -> bannerManager.bannerOptions.indicatorGravityBias
                        }
                    })
            }
        }
    }

    private fun setupViewPager() {
        adapter?.let {
            with(viewPager) {
                setScrollDuration(bannerManager.bannerOptions.scrollDuration)
                adapter = it.bannerPagerAdapter
                setCurrentItem(it.getOriginBeginIndex(), false)
            }
            startLoop()
        }
    }

    private fun getLoopItemIndexByOffset(offset: Int): Int {
        return adapter?.getLoopItemIndexByOffset(offset) ?: Adapter.LOOP_ORIGIN_INDEX
    }

    private fun logDebug(message: String) {
        if (logEnable) {
            logger.d(TAG, message)
        }
    }

    class Adapter<VH : ViewHolder>(private val viewHolder: VH) : ViewPager.OnPageChangeListener {
        companion object {
            internal const val LOOP_ORIGIN_INDEX = 1
        }

        private var currentLoopPosition: Int = 0
        var pageChangeListener: ViewPager.OnPageChangeListener? = null
        var currentPosition: Int = 0
            internal set

        var bannerView: BannerView? = null
            internal set(value) {
                field = value
                field?.viewPager?.removeOnPageChangeListener(this)
                field?.viewPager?.addOnPageChangeListener(this)
            }

        internal var bannerPagerAdapter: BannerPageAdapter = BannerPageAdapter()

        fun setPageClickListener(pageClickListener: OnPageClickListener) {
            bannerPagerAdapter.pageClickListener = pageClickListener
        }

        /**
         *  数据变更的时候必须要调用此方法
         */
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

        private fun getItemCount(): Int = bannerPagerAdapter.count

        fun getRealItemCount(): Int = viewHolder.getItemCount()

        internal fun getAutoNextPosition(curIndex: Int): Int {
            return curIndex + 1
        }

        /**
         * 可循环时的初始位置
         */
        private fun loopOriginIndex() =
            LOOP_ORIGIN_INDEX


        private fun setToLoopOriginIndex() {
            if (getRealItemCount() > 1 && bannerView!!.isCanLoop()) {
                currentLoopPosition = loopOriginIndex()
            }
        }

        internal fun getOriginBeginIndex(): Int {
            setToLoopOriginIndex()
            return if (getRealItemCount() > 1 && bannerView!!.isCanLoop()) currentLoopPosition else currentPosition
        }

        internal fun getLoopItemIndexByOffset(offset: Int): Int {
            val tempIndex = loopOriginIndex() + offset
            return BannerUtils.getLoopIndex(tempIndex, getRealItemCount())
        }


        override fun onPageScrollStateChanged(state: Int) {
            bannerView?.logDebug("curLoopPosition : $currentLoopPosition , curPosition : $currentPosition , curItem : ${bannerView?.viewPager?.currentItem}")
            when (state) {
                ViewPager.SCROLL_STATE_SETTLING, ViewPager.SCROLL_STATE_DRAGGING -> {
                }
                ViewPager.SCROLL_STATE_IDLE -> {
                    if (getRealItemCount() > 1 && bannerView!!.isCanLoop()) {
                        val curIndex = bannerView!!.viewPager.currentItem
                        if (curIndex == 0) {
                            bannerView!!.setCurrentItem(getRealItemCount() - 1, false)
                        } else if (curIndex == getItemCount() - 1) {
                            bannerView!!.setCurrentItem(0, false)
                        }
                    }

                }
            }
            bannerView?.indicatorView?.onPageScrollStateChanged(state)
            pageChangeListener?.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int) {
            val realItemCount = getRealItemCount()
            if (realItemCount > 0) {
                val realPosition =
                    BannerUtils.getRealPosition2(bannerView!!.isCanLoop(), position, realItemCount)
                pageChangeListener?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels)
                bannerView?.indicatorView?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels)
                bannerView?.logDebug("onPageScrolled: realPosition-$realPosition")
            }
        }

        override fun onPageSelected(position: Int) {
            bannerView ?: return
            bannerView?.logDebug("onPageSelected: position - $position")
            val size = getRealItemCount()
            currentPosition =
                BannerUtils.getRealPosition2(bannerView!!.isCanLoop(), position, size)


            currentLoopPosition = position

            bannerView?.logDebug("onPageSelected: curPosition - $currentPosition , curLoopPosition - $currentLoopPosition")
            pageChangeListener?.onPageSelected(currentPosition)
            bannerView?.indicatorView?.onPageSelected(currentPosition)
        }

        inner class BannerPageAdapter : PagerAdapter() {
            internal var pageClickListener: OnPageClickListener? = null
            override fun isViewFromObject(view: View, any: Any): Boolean = view == any

            override fun getCount(): Int {
                return if (bannerView!!.isCanLoop() && viewHolder.getItemCount() > 1)
                    viewHolder.getItemCount() + 2
                else
                    viewHolder.getItemCount()
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                return getItemView(container, position)
            }

            private fun getItemView(container: ViewGroup, position: Int): View {
                val realPosition = BannerUtils.getRealPosition2(
                    bannerView!!.isCanLoop(),
                    position,
                    viewHolder.getItemCount())

                val view = viewHolder.createBannerView(container, realPosition).also { v ->
                    v.setOnClickListener { pageClickListener?.onPageClick(v, realPosition) }
                }
                container.addView(view)
                return view
            }

            override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
                container.removeView(any as? View)
            }
        }

        interface OnPageClickListener {
            fun onPageClick(v: View, position: Int)
        }
    }

    abstract class ViewHolder {

        @LayoutRes
        abstract fun getLayoutId(viewType: Int): Int

        abstract fun getItemCount(): Int

        fun createBannerView(parent: ViewGroup, position: Int): View {
            val view = LayoutInflater.from(parent.context)
                .inflate(getLayoutId(getItemViewType(position)), parent, false)
            return onViewCreate(view, parent, position)
        }

        open fun getItemViewType(position: Int): Int = 0

        protected open fun onViewCreate(view: View, parent: ViewGroup, position: Int): View {
            return view
        }
    }

    class BannerViewPager(context: Context) : ViewPager(context) {

        private var bannerScroller: BannerScroller? = null

        init {
            hookScroller()
        }

        fun setScrollDuration(scrollDuration: Int) {
            bannerScroller?.setCustomDuration(scrollDuration)
        }

        private fun hookScroller() {
            try {
                bannerScroller = BannerScroller(
                    context
                ).also {
                    it.setCustomDuration(DEFAULT_SCROLL_DURATION)
                    val field = ViewPager::class.java.getDeclaredField("mScroller")
                    field.isAccessible = true
                    field.set(this, it)
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
        }


        class BannerScroller @JvmOverloads constructor(
            context: Context,
            interpolator: Interpolator? = null)
            : Scroller(context, interpolator, true) {
            private var mDuration: Int = 1000

            override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
                super.startScroll(startX, startY, dx, dy, mDuration)
            }

            override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
                super.startScroll(startX, startY, dx, dy, mDuration)
            }

            fun setCustomDuration(duration: Int) {
                mDuration = duration
            }
        }
    }
}