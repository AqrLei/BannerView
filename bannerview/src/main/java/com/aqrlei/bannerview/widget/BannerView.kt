package com.aqrlei.bannerview.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.widget.enums.BannerIndicatorGravity
import com.aqrlei.bannerview.widget.enums.BannerIndicatorPosition
import com.aqrlei.bannerview.widget.enums.TransformerStyle
import com.aqrlei.bannerview.widget.indicator.IndicatorView
import com.aqrlei.bannerview.widget.indicator.base.IIndicator
import com.aqrlei.bannerview.widget.manager.BannerManager
import com.aqrlei.bannerview.widget.transform.factory.BannerPageTransformerFactory
import com.aqrlei.bannerview.widget.utils.BannerUtils

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
        var logEnable: Boolean = false

        //无限轮播时，设置的item数目
        var MAX_VALUE = 500
    }

    private val bannerManager: BannerManager = BannerManager()

    private var indicatorView: IIndicator? = null
    private val indicatorLayout: ConstraintLayout
    private val viewPager2: ViewPager2

    var currentPosition: Int = 0
    var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null
    var pageClickCallback: OnPageClickCallback? = null
    private var bannerAdapter: BannerAdapter? = null


    private val internalPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            bannerAdapter ?: return
            val listSize = bannerAdapter!!.getListSize()
            currentPosition = BannerUtils.getRealPosition2(isCanLoop, position, listSize)
            val needResetCurrentItem =
                listSize > 0 && isCanLoop && (position == 0 || position == MAX_VALUE - 1)
            if (needResetCurrentItem) {
                resetCurrentItem(currentPosition)
            }
            indicatorView?.onPageSelected(currentPosition)
            pageChangeCallback?.onPageSelected(currentPosition)
        }

        override fun onPageScrollStateChanged(state: Int) {
            bannerAdapter ?: return
            indicatorView?.onPageScrollStateChanged(state)
            pageChangeCallback?.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(
            position: Int, positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            bannerAdapter ?: return
            val listSize = bannerAdapter!!.getListSize()
            val realPosition = BannerUtils.getRealPosition2(isCanLoop, position, listSize)

            if (listSize > 0) {
                pageChangeCallback?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels
                )
                indicatorView?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            }
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
            field = bannerManager.bannerOptions.isCanLoop
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
        inflate(context, R.layout.layout_banner_child, this)
        viewPager2 = findViewById(R.id.viewPager2)
        indicatorLayout = findViewById(R.id.indicator)
    }

    fun logDebug(message: String) {
        if (logEnable) {
            Log.d(TAG, message)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startLoop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopLoop()
    }

    //TODO 目前是有问题的
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        bannerAdapter ?: return super.onInterceptTouchEvent(ev)
        val itemCount = bannerAdapter!!.getListSize()
        val realCanLoop = isCanLoop && itemCount > 1
        val canIntercept = viewPager2.isUserInputEnabled || itemCount > 1
        if (!canIntercept) {
            return super.onInterceptTouchEvent(ev)
        }

        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(viewPager2.currentItem < itemCount - 1 || realCanLoop)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.onInterceptTouchEvent(ev)

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
        bannerAdapter ?: return
        if (!isLooping && isAutoPlay && bannerAdapter!!.getListSize() > 1) {
            postDelayed(runnable, interval.toLong())
            isLooping = true
        }

    }

    private fun stopLoop() {
        if (isLooping) {
            removeCallbacks(runnable)
            isLooping = false
        }
    }

    fun setBannerViewHolder(viewHolder: BannerViewHolder) {
        bannerAdapter = BannerAdapter(viewHolder)
        initBanner()
    }

    //TODO
    fun setCurrentItem(item: Int) {
        bannerAdapter ?: return
        val checkCanLoop = isCanLoop && bannerAdapter!!.getListSize() > 1
        val index = if (checkCanLoop) getLoopItemIndex(item) else item
        viewPager2.currentItem = index

    }

    //TODO
    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        bannerAdapter ?: return
        val checkCanLoop = isCanLoop && bannerAdapter!!.getListSize() > 1
        val index = if (checkCanLoop) getLoopItemIndex(item) else item
        viewPager2.setCurrentItem(index, smoothScroll)
    }

    private fun initBanner() {
        setIndicatorValues()
        setupViewPager2()
        setChildLayoutParams()
    }

    private fun setChildLayoutParams() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        // viewpager2
        val isIndicatorBelow =
            bannerManager.bannerOptions.bannerIndicatorPosition == BannerIndicatorPosition.BELOW
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
                bannerManager.bannerOptions.indicatorVerticalBias
            )
        }

        // 配置了使用宽高比
        val bannerRatio = bannerManager.bannerOptions.widthHeightRatio
        bannerRatio.takeIf { it.isNotBlank() && bannerManager.bannerOptions.bannerUseRatio }?.let {
            constraintSet.setDimensionRatio(viewPager2.id, bannerRatio)
        }

        constraintSet.applyTo(this)
    }

    private fun handlePosition() {
        bannerAdapter ?: return
        if (bannerAdapter!!.getListSize() > 1) {
            viewPager2.currentItem = viewPager2.currentItem + 1
            postDelayed(runnable, interval.toLong())
        }
    }


    fun refresh(diffCallback: DiffUtil.Callback? = null) {
        bannerAdapter ?: return
        stopLoop()
        if (null != diffCallback) {
            val result = DiffUtil.calculateDiff(diffCallback)
            result.dispatchUpdatesTo(bannerAdapter!!)
        } else {
            bannerAdapter!!.notifyDataSetChanged()
        }
        resetCurrentItem(currentPosition)
        refreshIndicator()
        startLoop()
    }

    private fun refreshIndicator() {
        setIndicatorValues()
        bannerManager.bannerOptions.indicatorOptions.currentPosition =
            BannerUtils.getRealPosition2(
                isCanLoop,
                viewPager2.currentItem,
                bannerAdapter!!.getListSize()
            )
        indicatorView?.notifyChanged()
    }

    private fun resetCurrentItem(item: Int) {
        bannerAdapter ?: return
        if (isCanLoop && bannerAdapter!!.getListSize() > 1) {
            viewPager2.setCurrentItem(bannerAdapter!!.getOriginalPosition() + item, false)
        } else {
            viewPager2.setCurrentItem(item, false)
        }
    }

    private fun setIndicatorValues() {
        bannerAdapter?.let {
            if (null == indicatorView) {
                indicatorView = IndicatorView(context)
            }

            bannerManager.bannerOptions.indicatorOptions {
                customIndicator = indicatorView !is IndicatorView
            }

            indicatorView?.setIndicatorOptions(bannerManager.bannerOptions.indicatorOptions.also { indicatorOptions ->
                indicatorOptions.pageSize = it.getListSize()
            })
            initIndicator()
            indicatorView?.notifyChanged()
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
                        lp.horizontalBias =
                            when (bannerManager.bannerOptions.bannerIndicatorGravity) {
                                BannerIndicatorGravity.START -> 0F
                                BannerIndicatorGravity.CENTER -> 0.5F
                                BannerIndicatorGravity.END -> 1.0F
                                BannerIndicatorGravity.BIAS -> bannerManager.bannerOptions.indicatorGravityBias
                            }
                    })
            }
        }
    }

    private fun setupViewPager2() {
        bannerAdapter ?: return
        currentPosition = 0
        with(viewPager2) {
            //MULTI
            if (bannerManager.bannerOptions.transformerStyle in arrayOf(
                    TransformerStyle.MULTI,
                    TransformerStyle.MULTI_OVERLAP
                )
            ) {
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
                    bannerManager.bannerOptions.transformerScale
                )
            )
            adapter = bannerAdapter
            val checkCanLoop = isCanLoop && bannerAdapter!!.getListSize() > 1
            if (checkCanLoop) {
                setCurrentItem(bannerAdapter!!.getOriginalPosition(), false)
            }
        }
        startLoop()
    }

    private fun getLoopItemIndex(item: Int): Int {
        val currentItem = viewPager2.currentItem
        val listSize = bannerAdapter!!.getListSize()
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
            viewPager2.registerOnPageChangeCallback(internalPageChangeCallback)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
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