package com.hzh.zprogressbar.base

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.core.util.Pools
import com.hzh.zprogressbar.R
import java.util.*

/**
 * Create by hzh on 2020/12/24.
 */
abstract class BaseProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    protected var mWidth = 0
        private set

    protected var mHeight = 0
        private set

    @ColorInt
    private var mBotColor: Int = Color.parseColor("#ffededed") // 底部颜色

    @ColorInt
    private var mBotGradientStart: Int = -1 // 底部渐变起点颜色

    @ColorInt
    private var mBotGradientEnd: Int = -1 // 底部渐变终点颜色

    @ColorInt
    private var mSecondaryColor: Int = Color.parseColor("#ff00bfff") // 第二进度颜色

    @ColorInt
    private var mSecondaryGradientStart: Int = -1 // 第二进度渐变起点颜色

    @ColorInt
    private var mSecondaryGradientEnd: Int = -1 // 第二进度渐变终点颜色

    @ColorInt
    private var mProgressColor: Int = Color.parseColor("#fffdc100") // 当前进度颜色

    @ColorInt
    private var mProgressGradientStart: Int = -1 // 当前进度渐变起点颜色

    @ColorInt
    private var mProgressGradientEnd: Int = -1 // 当前进度渐变终点颜色

    private var mMaxProgress = 100 // 最大进度
    private var mSecondaryProgress = 0 // 第二进度
    private var mCurrSecondaryProgress = 0 // 用来更新ui的
    private var mProgress = 0 // 当前进度
    private var mOldProgress = 0 // 控制动画的
    private var mCurrProgress = 0 // 用来更新ui的

    private val mUiThreadId = Thread.currentThread().id
    private val mRefreshData = ArrayList<RefreshData>()

    private var mAttached = false
    private var mRefreshIsPosted = false
    private var mAnimDuration = 500L // 动画时长
    private var mRefreshProgressRunnable: RefreshProgressRunnable? = null
    private var mAnim: ValueAnimator? = null

    private val mBotPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mBotColor }
    }

    private val mSecondaryPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mSecondaryColor }
    }

    private val mProgressPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mProgressColor }
    }

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.BaseProgressBar).run {
            mMaxProgress = getInt(R.styleable.BaseProgressBar_max, 100)
            mSecondaryProgress = getInt(R.styleable.BaseProgressBar_secondaryProgress, 0)
            mProgress = getInt(R.styleable.BaseProgressBar_progress, 0)

            mBotColor =
                getColor(R.styleable.BaseProgressBar_botColor, Color.parseColor("#ffededed"))
            mBotGradientStart = getColor(R.styleable.BaseProgressBar_botGradientStart, -1)
            mBotGradientEnd = getColor(R.styleable.BaseProgressBar_botGradientEnd, -1)

            mSecondaryColor =
                getColor(R.styleable.BaseProgressBar_secondaryColor, Color.parseColor("#ff00bfff"))
            mSecondaryGradientStart =
                getColor(R.styleable.BaseProgressBar_secondaryGradientStart, -1)
            mSecondaryGradientEnd = getColor(R.styleable.BaseProgressBar_secondaryGradientEnd, -1)

            mProgressColor =
                getColor(R.styleable.BaseProgressBar_progressColor, Color.parseColor("#fffdc100"))
            mProgressGradientStart = getColor(R.styleable.BaseProgressBar_progressGradientStart, -1)
            mProgressGradientEnd = getColor(R.styleable.BaseProgressBar_progressGradientEnd, -1)

            mAnimDuration = getInt(R.styleable.BaseProgressBar_animDuration, 500).toLong()

            recycle()
        }
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        layoutParams.run {
            if (width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                height == ViewGroup.LayoutParams.WRAP_CONTENT
            ) setMeasuredDimension(0, 0)
            else if (width == ViewGroup.LayoutParams.WRAP_CONTENT)
                setMeasuredDimension(0, heightSize)
            else if (height == ViewGroup.LayoutParams.WRAP_CONTENT)
                setMeasuredDimension(widthSize, 0)
        }
    }

    @Synchronized
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = measuredWidth
        mHeight = measuredHeight

        if (mWidth == 0 || mHeight == 0) throw IllegalArgumentException("width and height require not zero!")

        // 设置底部的渐变色
        if (mBotGradientStart != -1 && mBotGradientEnd != -1)
            createGradient(intArrayOf(mBotGradientStart, mBotGradientEnd), mBotPaint)

        // 设置第二进度的渐变色
        if (mSecondaryGradientStart != -1 && mSecondaryGradientEnd != -1)
            createGradient(intArrayOf(mSecondaryGradientStart, mSecondaryGradientEnd), mSecondaryPaint)

        // 设置当前进度的渐变色
        if (mProgressGradientStart != -1 && mProgressGradientEnd != -1)
            createGradient(intArrayOf(mProgressGradientStart, mProgressGradientEnd), mProgressPaint)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            // 画进度条底部
            drawPrimary(this, mBotPaint)

            // 画第二进度
            drawSecondary(this, mCurrSecondaryProgress.toFloat() / mMaxProgress, mSecondaryPaint)

            // 画当前进度
            drawProgress(this, mCurrProgress.toFloat() / mMaxProgress, mProgressPaint)
        }
    }

    @Synchronized
    fun setSecondaryProgress(progress: Int) {
        if (progress < 0) return

        val p = if (progress > mMaxProgress) mMaxProgress else progress

        if (mSecondaryProgress == p) return

        mSecondaryProgress = p

        refreshProgress(isSecondary = true, isAnimate = false, mSecondaryProgress)
    }

    @Synchronized
    fun setProgress(progress: Int, isAnimate: Boolean = false) {
        if (progress < 0) return

        val p = if (progress > mMaxProgress) mMaxProgress else progress

        if (mProgress == p) return

        mProgress = p

        refreshProgress(false, isAnimate, mProgress)
    }

    @Synchronized
    private fun refreshProgress(isSecondary: Boolean, isAnimate: Boolean, progress: Int) {
        if (Thread.currentThread().id == mUiThreadId) doRefreshProgress(isSecondary, isAnimate, progress)
        else {
            if (mRefreshProgressRunnable == null)
                mRefreshProgressRunnable = RefreshProgressRunnable()

            mRefreshData.add(RefreshData.obtain(isSecondary, isAnimate, progress))

            if (mAttached && !mRefreshIsPosted) {
                post(mRefreshProgressRunnable)
                mRefreshIsPosted = true
            }
        }
    }

    @Synchronized
    private fun doRefreshProgress(isSecondary: Boolean, isAnimate: Boolean, progress: Int) {
        if (!isSecondary && isAnimate) {
            mAnim?.run { if (isRunning) cancel() }

            mAnim = ValueAnimator.ofFloat(mOldProgress.toFloat(), progress.toFloat()).apply {
                duration = mAnimDuration
                interpolator = AccelerateDecelerateInterpolator()

                addUpdateListener {
                    val curr = (it.animatedValue as Float).toInt()
                    mOldProgress = curr
                    mCurrProgress = curr
                    invalidate()
                }

                start()
            }
        } else {
            if (!isSecondary) {
                mOldProgress = progress
                mCurrProgress = progress
            } else mCurrSecondaryProgress = progress

            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        synchronized(this) {
            val count = mRefreshData.size

            for (i in 0 until count) {
                mRefreshData[i].run {
                    doRefreshProgress(isSecondary, isAnimate, progress)
                    recycle()
                }
            }

            mRefreshData.clear()
        }

        mAttached = true
    }

    override fun onDetachedFromWindow() {
        mAnim?.run { if (isRunning) cancel() }
        mAnim = null

        mRefreshProgressRunnable?.run {
            removeCallbacks(mRefreshProgressRunnable)
            mRefreshIsPosted = false
        }

        super.onDetachedFromWindow()
        mAttached = false
    }

    @Synchronized
    protected open fun createGradient(colors: IntArray, paint: Paint) {
        throw IllegalArgumentException("Not support gradient! Sub class need to override this fun.")
    }

    /**
     * 画进度条底部
     */
    protected abstract fun drawPrimary(c: Canvas, paint: Paint)

    /**
     * 画第二进度
     */
    protected abstract fun drawSecondary(c: Canvas, ratio: Float, paint: Paint)

    /**
     * 画当前进度
     */
    protected abstract fun drawProgress(c: Canvas, ratio: Float, paint: Paint)

    private class RefreshData private constructor() {

        companion object {

            private const val POOL_MAX = 24
            private val sPool = Pools.SynchronizedPool<RefreshData>(POOL_MAX)

            fun obtain(
                isSecondary: Boolean,
                isAnimate: Boolean,
                progress: Int,
            ): RefreshData {
                var rd = sPool.acquire()
                if (rd == null) rd = RefreshData()

                rd.isSecondary = isSecondary
                rd.isAnimate = isAnimate
                rd.progress = progress

                return rd
            }
        }

        var isSecondary = false
        var isAnimate = false
        var progress = 0

        fun recycle() = sPool.release(this)
    }

    private inner class RefreshProgressRunnable : Runnable {

        override fun run() {
            synchronized(this@BaseProgressBar) {
                val count = mRefreshData.size

                for (i in 0 until count) {
                    mRefreshData[i].run {
                        doRefreshProgress(isSecondary, isAnimate, progress)
                        recycle()
                    }
                }

                mRefreshData.clear()
                mRefreshIsPosted = false
            }
        }
    }
}