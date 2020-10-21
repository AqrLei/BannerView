package com.aqrlei.bannerview.widget

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
import com.aqrlei.bannerview.widget.adapter.Adapter
import com.aqrlei.bannerview.widget.enums.IndicatorGravity
import com.aqrlei.bannerview.widget.enums.IndicatorPosition
import com.aqrlei.bannerview.widget.enums.TransformerStyle
import com.aqrlei.bannerview.widget.indicator.IIndicator
import com.aqrlei.bannerview.widget.indicator.IndicatorView
import com.aqrlei.bannerview.widget.utils.BannerUtils
import com.aqrlei.bannerview.widget.manager.BannerManager
import com.aqrlei.bannerview.widget.transform.factory.BannerPageTransformerFactory
import com.aqrlei.bannerview.widget.viewholder.ViewHolder

/**
 * created by AqrLei on 2020/4/23
 */
class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "BannerView"
        private var logEnable: Boolean = false
        fun logEnable(enable: Boolean) {
            logEnable = enable
        }
    }

    internal val bannerManager: BannerManager = BannerManager()

    var indicatorView: IIndicator? = null

    private val indicatorLayout: ConstraintLayout = ConstraintLayout(context)
    val viewPager2: ViewPager2 = ViewPager2(context)

    var adapter: Adapter<*>? = null
        set(value) {
            field = value
            field?.bannerView = this
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

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        val itemCount = adapter?.getRealItemCount() ?: 0
        val realCanLoop = isCanLoop && itemCount > 1
        val canIntercept = viewPager2.isUserInputEnabled || itemCount > 1
        if (!canIntercept) {
            return super.onInterceptTouchEvent(ev)
        }

        when(ev?.action) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(viewPager2.currentItem < itemCount -1 || realCanLoop)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(false)
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

    internal fun refreshBanner() {
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

    fun logDebug(message: String) {
        if (logEnable) {
            logger.d(TAG, message)
        }
    }
}