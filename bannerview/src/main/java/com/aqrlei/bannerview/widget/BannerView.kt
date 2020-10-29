package com.aqrlei.bannerview.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.enums.BannerIndicatorGravity
import com.aqrlei.bannerview.widget.enums.BannerIndicatorPosition
import com.aqrlei.bannerview.widget.enums.PageTransformerStyle
import com.aqrlei.bannerview.widget.indicator.IndicatorView
import com.aqrlei.bannerview.widget.indicator.base.IIndicator
import com.aqrlei.bannerview.widget.indicator.enums.IndicatorSlideMode
import com.aqrlei.bannerview.widget.indicator.options.IndicatorOptions
import com.aqrlei.bannerview.widget.manager.BannerManager
import com.aqrlei.bannerview.widget.options.BannerIndicatorChildOptions
import com.aqrlei.bannerview.widget.options.BannerIndicatorParentOptions
import com.aqrlei.bannerview.widget.options.BannerOptions
import com.aqrlei.bannerview.widget.transform.ScaleInOverlapTransformer
import com.aqrlei.bannerview.widget.transform.ScaleInTransformer
import com.aqrlei.bannerview.widget.utils.BannerUtils
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * created by AqrLei on 2020/4/23
 */
class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "BannerView"

        //无限轮播时，设置的item数目
        var MAX_VALUE = 500
    }

    private val bannerManager: BannerManager = BannerManager()

    private val touchSlop: Int
    private var initialX: Float = 0F
    private var initialY: Float = 0F

    private var indicatorView: IIndicator? = null
    private val indicatorLayout: ConstraintLayout
    private val viewPager2: ViewPager2
    private val compositePageTransformer = CompositePageTransformer()
    private var marginPageTransformer: MarginPageTransformer? = null

    private var defaultPageTransformer: ViewPager2.PageTransformer? = null

    var isCustomIndicator: Boolean = false
        private set

    var currentPosition: Int = 0
    var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null
    var pageClickCallback: OnPageClickCallback? = null
    private var bannerAdapter: BannerAdapter? = null


    private val internalPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            bannerAdapter?.let {
                val listSize = it.getListSize()
                currentPosition = BannerUtils.getRealPosition2(isCanLoop, position, listSize)
                indicatorView?.onPageSelected(currentPosition)
                pageChangeCallback?.onPageSelected(currentPosition)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            bannerAdapter?.let {
                if (ViewPager2.SCROLL_STATE_IDLE == state) {
                    needResetCurrentItem(it.getListSize())
                }
                indicatorView?.onPageScrollStateChanged(state)
                pageChangeCallback?.onPageScrollStateChanged(state)
            }
        }

        override fun onPageScrolled(
            position: Int, positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            bannerAdapter?.let {
                val listSize = it.getListSize()
                val realPosition = BannerUtils.getRealPosition2(isCanLoop, position, listSize)

                if (listSize > 0) {
                    pageChangeCallback?.onPageScrolled(
                        realPosition,
                        positionOffset,
                        positionOffsetPixels
                    )
                    indicatorView?.onPageScrolled(
                        realPosition,
                        positionOffset,
                        positionOffsetPixels
                    )
                }
            }
        }
    }

    private val runnable = Runnable { handlePosition() }

    var isCanLoop: Boolean = getBannerOptions().isCanLoop
        get() = getBannerOptions().isCanLoop
        set(value) {
            getBannerOptions().isCanLoop = value
            if (!field) {
                getBannerOptions().isAutoPlay = false
            }
            field = getBannerOptions().isCanLoop
        }
    var isLooping
        get() = getBannerOptions().isLooping
        private set(value) {
            getBannerOptions().isLooping = value
        }
    var isAutoPlay
        get() = getBannerOptions().isAutoPlay
        set(value) {
            getBannerOptions().isAutoPlay = value
            if (value) {
                getBannerOptions().isCanLoop = true
                startAutoLoop()
            } else {
                stopAutoLoop()
            }
        }
    var interval
        get() = getBannerOptions().interval
        set(value) {
            getBannerOptions().interval = value
        }

    var indicatorVisibility: Int
        get() = getBannerIndicatorParentOptions().indicatorVisibility
        set(value) {
            if (indicatorVisibility != value) {
                getBannerIndicatorParentOptions().indicatorVisibility = value
                indicatorLayout.visibility = getBannerIndicatorParentOptions().indicatorVisibility
            }
        }

    init {
        bannerManager.initAttrs(context, attrs)
        inflate(context, R.layout.layout_banner_child, this)
        viewPager2 = findViewById(R.id.viewPager2)
        indicatorLayout = findViewById(R.id.indicator)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAutoLoop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoLoop()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        handleInterceptTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)

    }

    private fun handleInterceptTouchEvent(ev: MotionEvent?) {
        bannerAdapter?.let {
            val lastIndex = it.getListSize() - 1
            val canIntercept = viewPager2.isUserInputEnabled || lastIndex > 0

            if ((!canChildScroll(viewPager2.orientation, -1.0F)
                        && !canChildScroll(viewPager2.orientation, 1.0F)) || !canIntercept
            ) {
                return
            }

            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = ev.x
                    initialY = ev.y
                    parent.requestDisallowInterceptTouchEvent(lastIndex > 0)
                }
                MotionEvent.ACTION_MOVE -> {
                    val isHorizontal =
                        getBannerOptions().orientation == ViewPager2.ORIENTATION_HORIZONTAL

                    val dx = ev.x - initialX
                    val dy = ev.y - initialY

                    if (dx.absoluteValue > touchSlop || dy.absoluteValue > touchSlop) {
                        val delta = if (isHorizontal) dx else dy
                        parent.requestDisallowInterceptTouchEvent(
                            canChildScroll(
                                getBannerOptions().orientation,
                                delta
                            )
                        )
                    }
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            // Negative to check scrolling left, positive to check scrolling right.
            ViewPager2.ORIENTATION_HORIZONTAL -> viewPager2.canScrollHorizontally(direction)
            // Negative to check scrolling up, positive to check scrolling down.
            ViewPager2.ORIENTATION_VERTICAL -> viewPager2.canScrollVertically(direction)
            else -> false
        }
    }

    //横向
    private fun horizontalCanSlide(lastIndex: Int, ev: MotionEvent): Boolean {
        val moveX = ev.x
        return when (viewPager2.currentItem) {
            0 -> moveX - initialX < 0
            lastIndex -> moveX - initialX > 0
            else -> true
        }
    }

    // 纵向
    private fun verticalCanSlide(lastIndex: Int, ev: MotionEvent): Boolean {
        val moveY = ev.y
        return when (viewPager2.currentItem) {
            0 -> moveY - initialY < 0
            lastIndex -> moveY - initialY > 0
            else -> true
        }
    }

    /**
     * 点击控件的时候，轮播停止
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                isLooping = true
                stopAutoLoop()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_OUTSIDE -> {
                isLooping = false
                startAutoLoop()
            }
        }
        return super.dispatchTouchEvent(ev)

    }

    fun getBannerOptions(): BannerOptions = bannerManager.bannerOptions
    fun getIndicatorOptions(): IndicatorOptions = getBannerOptions().indicatorOptions
    fun getBannerIndicatorParentOptions(): BannerIndicatorParentOptions =
        getBannerOptions().bannerIndicatorParentOptions

    fun getBannerIndicatorChildOptions(): BannerIndicatorChildOptions =
        getBannerOptions().bannerIndicatorChildOptions

    fun setBannerOptions(block: BannerOptions.() -> Unit) {
        getBannerOptions().apply(block)
        setupViewPager2()
    }

    fun setBannerDimensionRatio(isUseRatio: Boolean, ratio: String) {
        getBannerOptions().bannerUseRatio = isUseRatio
        getBannerOptions().widthHeightRatio = ratio
        refreshDimensionRatio(ConstraintSet().also { it.clone(this) })
    }

    fun setIndicatorOptions(block: IndicatorOptions.() -> Unit) {
        getIndicatorOptions().apply(block)
        updateIndicator()
    }

    fun setBannerIndicatorParentOptions(block: BannerIndicatorParentOptions.() -> Unit) {
        indicatorVisibility = getBannerIndicatorParentOptions().indicatorVisibility
        getBannerIndicatorParentOptions().apply(block)
        refreshChildLayoutParams()
    }

    fun setBannerIndicatorChildOptions(block: BannerIndicatorChildOptions.() -> Unit) {
        getBannerIndicatorChildOptions().apply(block)
        setIndicatorValues()
    }

    fun setCustomIndicator(indicator: IIndicator?) {
        indicatorView = indicator
        if (null != indicator) {
            getIndicatorOptions().slideMode = IndicatorSlideMode.NORMAL
        }
        isCustomIndicator = null != indicator
        refreshIndicator()
    }

    fun setCurrentItem(item: Int) {
        bannerAdapter?.let {
            stopAutoLoop()
            val checkCanLoop = isCanLoop && it.getListSize() > 1
            val index = if (checkCanLoop) getLoopItemIndex(item, it.getListSize()) else item
            viewPager2.currentItem = index
            startAutoLoop()
        }
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        bannerAdapter?.let {
            stopAutoLoop()
            val checkCanLoop = isCanLoop && it.getListSize() > 1
            val index = if (checkCanLoop) getLoopItemIndex(item, it.getListSize()) else item
            viewPager2.setCurrentItem(index, smoothScroll)
            startAutoLoop()
        }
    }

    private fun getLoopItemIndex(item: Int, listSize: Int): Int {
        val currentItem = viewPager2.currentItem
        val realPosition =
            BannerUtils.getRealPosition2(isCanLoop, currentItem, listSize)

        val tempNext = currentItem + (item - realPosition)

        return when {
            tempNext < 0 -> currentItem + listSize - item - 1
            tempNext >= MAX_VALUE -> currentItem - item
            else -> tempNext
        }
    }

    /**
     * 设置BannerViewHolder， 开始初始化Banner
     */
    fun setBannerViewHolder(viewHolder: BannerViewHolder) {
        bannerAdapter = BannerAdapter(viewHolder)
        initBanner()
    }

    /**
     * 初始化Banner
     */
    private fun initBanner() {
        setIndicatorValues()
        currentPosition = 0
        setupViewPager2()
        refreshChildLayoutParams()
        startAutoLoop()
    }

    /**
     * 设置viewPager2的属性
     */
    private fun setupViewPager2() {
        bannerAdapter?.let { bAdapter ->
            with(viewPager2) {
                orientation = getBannerOptions().orientation
                offscreenPageLimit = getBannerOptions().offsetPageLimit
                val pageMargin = getBannerOptions().pageMargin.toInt()
                (getChildAt(0) as? RecyclerView)?.apply {
                    val paddingStart = getBannerOptions().startRevealWidth.toInt() + pageMargin
                    val paddingEnd = getBannerOptions().endRevealWidth.toInt() + pageMargin
                    if (getBannerOptions().orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                        setPadding(paddingStart, 0, paddingEnd, 0)
                    } else {
                        setPadding(0, paddingStart, 0, paddingEnd)
                    }
                    clipToPadding = false
                }
                adapter = bannerAdapter

                marginPageTransformer?.let { compositePageTransformer.removeTransformer(it) }

                if (pageMargin > 0) {
                    marginPageTransformer = MarginPageTransformer(pageMargin).also {
                        compositePageTransformer.addTransformer(it)
                    }
                }

                refreshPageStyle()

                setPageTransformer(compositePageTransformer)

                val checkCanLoop = isCanLoop && bAdapter.getListSize() > 1

                if (checkCanLoop) {
                    setCurrentItem(bAdapter.getOriginalPosition(), false)
                }
            }
        }
    }


    fun removePageTransformer(pageTransformer: ViewPager2.PageTransformer?) {
        pageTransformer?.let { compositePageTransformer.removeTransformer(pageTransformer) }
    }

    fun addPageTransformer(pageTransformer: ViewPager2.PageTransformer?) {
        pageTransformer?.let { compositePageTransformer.addTransformer(pageTransformer) }
    }

    fun removeDefaultPageTransformer() {
        defaultPageTransformer?.let { compositePageTransformer.removeTransformer(it) }
    }

    private fun refreshPageStyle() {
        removeDefaultPageTransformer()
        val scale = getBannerOptions().transformerScale
        defaultPageTransformer = when (getBannerOptions().pageTransformerStyle) {
            PageTransformerStyle.MULTI_OVERLAP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ScaleInOverlapTransformer(scale)
                } else {
                    ScaleInTransformer(scale)
                }
            }
            PageTransformerStyle.MULTI_SCALE_IN -> {
                ScaleInTransformer(scale)
            }
            PageTransformerStyle.NORMAL -> {
                null
            }
        }
        defaultPageTransformer?.let { compositePageTransformer.addTransformer(it) }
    }

    /**
     * 初始化布局属性
     */
    private fun refreshChildLayoutParams() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        // viewpager2
        val isIndicatorBelow =
            getBannerIndicatorParentOptions().bannerIndicatorParentPosition == BannerIndicatorPosition.BELOW
        if (isIndicatorBelow) {
            constraintSet.connect(
                viewPager2.id,
                ConstraintSet.BOTTOM,
                indicatorLayout.id,
                ConstraintSet.TOP
            )
        } else {
            constraintSet.connect(
                viewPager2.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
        }

        // indicator
        if (isIndicatorBelow) {
            constraintSet.connect(
                indicatorLayout.id,
                ConstraintSet.TOP,
                viewPager2.id,
                ConstraintSet.BOTTOM
            )
        } else {
            constraintSet.connect(
                indicatorLayout.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            constraintSet.setVerticalBias(
                indicatorLayout.id,
                getBannerIndicatorParentOptions().indicatorParentVerticalBias
            )
        }

        // 配置了使用宽高比
        refreshDimensionRatio(constraintSet)

        constraintSet.applyTo(this)
    }

    private fun refreshDimensionRatio(constraintSet: ConstraintSet) {
        val bannerRatio = getBannerOptions().widthHeightRatio
        bannerRatio.takeIf { it.isNotBlank() && getBannerOptions().bannerUseRatio }?.let {
            constraintSet.setDimensionRatio(viewPager2.id, bannerRatio)
        }
    }

    /**
     * 自动轮播处理
     */
    private fun handlePosition() {
        bannerAdapter?.let {
            if (it.getListSize() > 1) {
                viewPager2.currentItem = viewPager2.currentItem + 1
                postDelayed(runnable, interval.toLong())
            }
        }
    }

    private fun startAutoLoop() {
        bannerAdapter?.let {
            if (!isLooping && isAutoPlay && it.getListSize() > 1) {
                postDelayed(runnable, interval.toLong())
                isLooping = true
            }
        }
    }

    private fun stopAutoLoop() {
        if (isLooping) {
            removeCallbacks(runnable)
            isLooping = false
        }
    }

    /**
     * 数据变化通知刷新
     */
    fun notifyDataSetChanged(diffCallback: DiffUtil.Callback? = null) {
        bannerAdapter?.let {
            stopAutoLoop()
            if (null != diffCallback) {
                val result = DiffUtil.calculateDiff(diffCallback)
                result.dispatchUpdatesTo(it)
            } else {
                it.notifyDataSetChanged()
            }
            resetCurrentItem(currentPosition)
            refreshIndicator()
            startAutoLoop()
        }
    }

    private fun needResetCurrentItem(listSize: Int) {
        val position = viewPager2.currentItem
        val curPosition = BannerUtils.getRealPosition2(isCanLoop, position, listSize)
        val needResetCurrentItem =
            listSize > 0 && isCanLoop && (position == 0 || position == MAX_VALUE - 1)
        if (needResetCurrentItem) {
            resetCurrentItem(curPosition)
        }
    }

    /**
     * 自动轮播到临界值或数据更新，重新设置当前位置page
     */
    private fun resetCurrentItem(item: Int) {
        bannerAdapter?.let {
            if (isCanLoop && it.getListSize() > 1) {
                viewPager2.setCurrentItem(it.getOriginalPosition() + item, false)
            } else {
                viewPager2.setCurrentItem(item, false)
            }
        }
    }

    /**
     * Indicator
     */
    private fun refreshIndicator() {
        bannerAdapter?.let {
            setIndicatorValues()
            getIndicatorOptions().currentPosition =
                BannerUtils.getRealPosition2(
                    isCanLoop,
                    viewPager2.currentItem,
                    it.getListSize()
                )
            updateIndicator()
        }
    }

    private fun updateIndicator() {
        if (isCustomIndicator) {
            getIndicatorOptions().slideMode = IndicatorSlideMode.NORMAL
        }
        indicatorView?.notifyChanged()
    }

    private fun setIndicatorValues() {
        bannerAdapter?.let {
            if (null == indicatorView) {
                indicatorView = IndicatorView(context)
            }

            getIndicatorOptions().pageSize = it.getListSize()
            indicatorView?.setIndicatorOptions(getIndicatorOptions())
            initIndicator()
            indicatorView?.notifyChanged()
        }
    }

    private fun initIndicator() {
        indicatorLayout.visibility = getBannerIndicatorParentOptions().indicatorVisibility
        (indicatorView as? View)?.let {
            if (it.id <= 0) {
                it.id = R.id.indicatorChildView
            }
            if (null == it.parent) {
                indicatorLayout.removeAllViews()
                indicatorLayout.addView(
                    it,
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                )
            }
            refreshIndicatorLayoutParams()
        }
    }

    private fun refreshIndicatorLayoutParams() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(indicatorLayout)
        val horizontalBias = when (getBannerIndicatorChildOptions().indicatorChildGravity) {
            BannerIndicatorGravity.START -> 0F
            BannerIndicatorGravity.CENTER -> 0.5F
            BannerIndicatorGravity.END -> 1.0F
            BannerIndicatorGravity.BIAS -> getBannerIndicatorChildOptions().indicatorChildGravityBias
        }
        val indicatorMargin = getBannerIndicatorChildOptions().indicatorChildMargin

        constraintSet.connect(
            R.id.indicatorChildView,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            indicatorMargin.start
        )
        constraintSet.connect(
            R.id.indicatorChildView,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            indicatorMargin.top
        )
        constraintSet.connect(
            R.id.indicatorChildView,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
            indicatorMargin.end
        )
        constraintSet.connect(
            R.id.indicatorChildView,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            indicatorMargin.bottom
        )

        constraintSet.setHorizontalBias(R.id.indicatorChildView, horizontalBias)
        constraintSet.applyTo(indicatorLayout)
    }

    /**
     * OnPageClickCallback
     */
    open class OnPageClickCallback {
        open fun onClick(view: View, position: Int) {}
    }

    /**
     * RecyclerView.Adapter
     */
    internal inner class BannerAdapter(private val viewHolder: BannerViewHolder) :
        RecyclerView.Adapter<VH>() {

        fun getListSize() = viewHolder.getListSize()

        fun getOriginalPosition() = MAX_VALUE / 2 - ((MAX_VALUE) / 2) % getListSize()

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            viewHolder.onAttachedToBannerView(this@BannerView)
            viewPager2.registerOnPageChangeCallback(internalPageChangeCallback)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
            viewHolder.onDetachedFromBannerView(this@BannerView)
            viewPager2.unregisterOnPageChangeCallback(internalPageChangeCallback)
        }

        override fun getItemViewType(position: Int): Int {
            val realPosition = BannerUtils.getRealPosition2(isCanLoop, position, getListSize())
            return viewHolder.getItemViewType(realPosition) ?: super.getItemViewType(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(viewHolder.onCreateBannerView(parent, viewType))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val realPosition = BannerUtils.getRealPosition2(isCanLoop, position, getListSize())
            viewHolder.onBindView(realPosition, holder.itemView.also { v ->
                v.setOnClickListener {
                    pageClickCallback?.onClick(v, realPosition)
                }
            })
        }

        override fun getItemCount(): Int {
            val checkCanLoop = isCanLoop && getListSize() > 1
            return if (checkCanLoop) MAX_VALUE else getListSize()
        }
    }

    internal class VH(view: View) : RecyclerView.ViewHolder(view)
}