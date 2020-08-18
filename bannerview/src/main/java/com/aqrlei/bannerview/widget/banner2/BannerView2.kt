package com.aqrlei.bannerview.widget.banner2

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.BuildConfig.logger
import com.aqrlei.bannerview.widget.R
import com.aqrlei.bannerview.widget.enums.IndicatorGravity
import com.aqrlei.bannerview.widget.enums.IndicatorPosition
import com.aqrlei.bannerview.widget.enums.TransformerStyle
import com.aqrlei.bannerview.widget.indicator.IIndicator
import com.aqrlei.bannerview.widget.indicator.IndicatorView
import com.aqrlei.bannerview.widget.utils.BannerUtils
import com.aqrlei.bannerview.widget.banner2.manager.BannerManager
import com.aqrlei.bannerview.widget.banner2.transform.factory.BannerPageTransformerFactory

/**
 * created by AqrLei on 2020/4/23
 */
class BannerView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "BannerView2"
        private var logEnable: Boolean = false
        fun logEnable(enable: Boolean) {
            logEnable = enable
        }
    }

    private val bannerManager: BannerManager = BannerManager()

    var indicatorView: IIndicator? = null

    private val indicatorLayout: ConstraintLayout = ConstraintLayout(context)
    private val viewPager2: ViewPager2 = ViewPager2(context)

    var adapter: Adapter<*>? = null
        set(value) {
            field = value
            field?.bannerView2 = this
            this.post {
                initBanner()
            }
        }

    private val runnable = Runnable { handlePosition() }

    var isCanLoop: Boolean = bannerManager.bannerOptions.isCanLoop
        get() = bannerManager.bannerOptions.isCanLoop
        set(value) {
            bannerManager.bannerOptions.isCanLoop = value
            if (!field) {
                bannerManager.bannerOptions.isAutoPlay = false
            }
        }
    var isLooping
        get() = bannerManager.bannerOptions.isLooping
        private set(value) {
            bannerManager.bannerOptions.isLooping = value
        }
    var isAutoPlay
        get() = bannerManager.bannerOptions.isAutoPlay
        set(value) {
            bannerManager.bannerOptions.isAutoPlay = value
            if (value) {
                bannerManager.bannerOptions.isCanLoop = true
            }
        }
    var interval
        get() = bannerManager.bannerOptions.interval
        set(value) {
            bannerManager.bannerOptions.interval = value
        }

    init {
        bannerManager.initAttrs(context, attrs)
        viewPager2.id = R.id.bvBanner
        indicatorLayout.id = R.id.bvIndicator
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startLoop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopLoop()
    }

    /**
     * 点击控件的时候，轮播停止
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                isLooping = true
                stopLoop()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_OUTSIDE -> {
                isLooping = false
                startLoop()
            }
        }
        return super.dispatchTouchEvent(ev)

    }

    private fun startLoop() {
        adapter?.let {
            if (!isLooping && isAutoPlay && it.getRealItemCount() > 1) {
                postDelayed(runnable, interval.toLong())
                isLooping = true
            }
        }
    }

    private fun stopLoop() {
        if (isLooping) {
            removeCallbacks(runnable)
            isLooping = false
        }
    }

    fun setCurrentItem(item: Int) {
        adapter?.let {
            val index = if (isCanLoop && it.getRealItemCount() > 1)
                getLoopItemIndex(item)
            else item
            viewPager2.currentItem = index
        }
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        adapter?.let {
            val index = if (isCanLoop && it.getRealItemCount() > 1)
                getLoopItemIndex(item)
            else item

            viewPager2.setCurrentItem(index, smoothScroll)
        }
    }

    private fun initBanner() {
        removeAllViews()
        setIndicatorValues()
        setupViewPager2()
        addBannerChildView()
    }

    private fun addBannerChildView() {
        // 添加banner容器ViewPager
        addView(viewPager2, LayoutParams(0, 0).apply {
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
        addView(indicatorLayout.also { it.background = ColorDrawable(Color.TRANSPARENT) },
            LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
                if (bannerManager.bannerOptions.indicatorPosition == IndicatorPosition.BELOW) {
                    topToBottom = viewPager2.id
                } else {
                    topToTop = LayoutParams.PARENT_ID
                    verticalBias = bannerManager.bannerOptions.indicatorVerticalBias
                }
                bottomToBottom = LayoutParams.PARENT_ID
            })

        // 配置了使用宽高比
        val bannerRatio = bannerManager.bannerOptions.widthHeightRatio
        bannerRatio.takeIf { it.isNotBlank() && bannerManager.bannerOptions.bannerUseRatio }?.let {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            constraintSet.setDimensionRatio(viewPager2.id, bannerRatio)
            constraintSet.applyTo(this)
        }
    }

    private fun handlePosition() {
        adapter?.let {
            if (it.getRealItemCount() > 1) {
                val curIndex = viewPager2.currentItem
                viewPager2.currentItem = it.getAutoNextPosition(curIndex)
                postDelayed(runnable, interval.toLong())
            }
        }
    }

    private fun refreshBanner() {
        stopLoop()
        setIndicatorValues()
        setupViewPager2()
    }

    private fun setIndicatorValues() {
        adapter?.let {
            if (null == indicatorView) {
                indicatorView = IndicatorView(context)
            }

            bannerManager.bannerOptions.indicatorOptions {
                customIndicator = indicatorView !is IndicatorView
            }

            indicatorView?.indicatorOptions = bannerManager.bannerOptions.indicatorOptions
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

    private fun setupViewPager2() {
        adapter?.let {
            with(viewPager2) {

                //MULTI
                if (bannerManager.bannerOptions.transformerStyle in arrayOf(
                        TransformerStyle.MULTI,
                        TransformerStyle.MULTI_OVERLAP)) {
                    offscreenPageLimit = bannerManager.bannerOptions.offsetPageLimit
                    (getChildAt(0) as? RecyclerView)?.apply {
                        val padding = bannerManager.bannerOptions.revealWidth.toInt()
                        setPadding(padding, 0, padding, 0)
                        clipToPadding = false
                    }
                }

                //PageTransformer
                setPageTransformer(
                    BannerPageTransformerFactory.createPageTransformer(
                        bannerManager.bannerOptions.transformerStyle,
                        bannerManager.bannerOptions.transformerScale))
                adapter = it.bannerPagerAdapter
                setCurrentItem(it.getOriginBeginIndex(), false)
            }
            startLoop()
        }
    }

    private fun getLoopItemIndex(offset: Int): Int {
        return adapter?.getLoopItemIndexByOffset(offset) ?: Adapter.LOOP_ORIGIN_INDEX
    }

    private fun logDebug(message: String) {
        if (logEnable) {
            logger.d(TAG, message)
        }
    }

    class Adapter<VH : ViewHolder>(val viewHolder: VH) : ViewPager2.OnPageChangeCallback() {
        companion object {
            internal const val LOOP_ORIGIN_INDEX = 1
        }

        var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null
        var pageClickCallback: OnPageClickCallback? = null

        var currentPosition: Int = 0
            internal set

        private var currentLoopPosition: Int = 0

        var bannerView2: BannerView2? = null
            internal set

        internal val bannerPagerAdapter: BannerPagerAdapter by lazy { BannerPagerAdapter() }

        fun notifyDataChange() {
            // 刷新的时候 回到第一个位置
            currentPosition = 0

            // 如果是可循环的，改变currentLoopPosition的值
            setToLoopOriginIndex()
            bannerView2?.bannerManager?.bannerOptions?.indicatorOptions {
                currentPosition = this@Adapter.currentPosition
            }
            bannerView2?.refreshBanner()
        }

        fun getRealItemCount(): Int = viewHolder.getItemCount()

        internal fun getAutoNextPosition(curIndex: Int): Int {
            return curIndex + 1
        }

        private fun setToLoopOriginIndex() {
            if (getRealItemCount() > 0 && bannerView2!!.isCanLoop) {
                currentLoopPosition = loopOriginIndex()
            }
        }

        internal fun getLoopItemIndexByOffset(offset: Int) =
            BannerUtils.getLoopIndex((loopOriginIndex() + offset), getRealItemCount())

        internal fun getOriginBeginIndex(): Int {
            setToLoopOriginIndex()
            return if (getRealItemCount() > 1 && bannerView2!!.isCanLoop) currentLoopPosition else currentPosition
        }

        override fun onPageSelected(position: Int) {
            bannerView2 ?: return
            bannerView2!!.logDebug("onPageSelected: position - $position")
            val size = getRealItemCount()
            currentPosition = BannerUtils.getRealPosition2(bannerView2!!.isCanLoop, position, size)
            currentLoopPosition = position

            bannerView2!!.logDebug("onPageSelected: currentPosition-$currentPosition , curLoopPosition : $currentLoopPosition")
            bannerView2!!.indicatorView?.onPageSelected(currentPosition)
            pageChangeCallback?.onPageSelected(currentPosition)
        }

        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager2.SCROLL_STATE_SETTLING, ViewPager2.SCROLL_STATE_DRAGGING -> {
                }
                ViewPager2.SCROLL_STATE_IDLE -> {
                    if (getRealItemCount() > 1 && bannerView2!!.isCanLoop) {
                        val curIndex = bannerView2!!.viewPager2.currentItem
                        if (curIndex == 0) {
                            bannerView2!!.setCurrentItem(getRealItemCount() - 1, false)
                        } else if (curIndex == getItemCount() - 1) {
                            bannerView2!!.setCurrentItem(0, false)
                        }
                    }
                }
            }
            bannerView2?.indicatorView?.onPageScrollStateChanged(state)
            pageChangeCallback?.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int) {
            bannerView2 ?: return
            val realItemCount = getRealItemCount()
            if (realItemCount > 0) {
                val realPosition =
                    BannerUtils.getRealPosition2(bannerView2!!.isCanLoop, position, realItemCount)
                bannerView2?.indicatorView?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels)
                bannerView2?.logDebug("onPageScrolled: realPosition-$realPosition")
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
                val realPosition = BannerUtils.getRealPosition2(bannerView2!!.isCanLoop,position,getRealItemCount())
                return viewHolder.getItemViewType(realPosition) ?: super.getItemViewType(position)
            }

            override fun getItemCount(): Int {
                return if (bannerView2!!.isCanLoop && viewHolder.getItemCount() > 1) viewHolder.getItemCount() + 2 else viewHolder.getItemCount()
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (itemCount > 0) {
                    val realPosition =
                        BannerUtils.getRealPosition2(
                            bannerView2!!.isCanLoop,
                            position,
                            this@Adapter.getRealItemCount())
                    val view = holder.itemView.also { v ->
                        v.setOnClickListener {
                            pageClickCallback?.onClick(v, realPosition)
                        }
                    }
                    bannerView2!!.logDebug("onBindViewHolder - position : $position")
                    bannerView2!!.logDebug("onBindViewHolder - realPosition : $realPosition")
                    bannerView2!!.logDebug("onBindViewHolder - itemCount : $itemCount")
                    viewHolder.bindView(realPosition, view)
                }
            }

            override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                super.onAttachedToRecyclerView(recyclerView)
                bannerView2?.viewPager2?.registerOnPageChangeCallback(this@Adapter)
            }

            override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
                bannerView2?.viewPager2?.unregisterOnPageChangeCallback(this@Adapter)
                super.onDetachedFromRecyclerView(recyclerView)
            }
        }

        open class OnPageClickCallback {
            open fun onClick(view: View, position: Int) {}
        }

        inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    abstract class ViewHolder {
        @LayoutRes
        abstract fun getLayoutId(viewType: Int): Int
        abstract fun getItemCount(): Int
        fun createBannerView(parent: ViewGroup,viewType: Int): View {
            val view = LayoutInflater.from(parent.context).inflate(getLayoutId(viewType), parent, false)
            return onViewCreate(view, parent)
        }

        protected open fun onViewCreate(view: View, parent: ViewGroup): View {
            return view
        }

        open fun getItemViewType(position: Int): Int? {
            return null
        }

        abstract fun bindView(position: Int, view: View)
    }
}