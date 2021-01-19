package com.hzh.zprogressbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.hzh.zprogressbar.base.BaseProgressBar
import com.hzh.zprogressbar.utils.dp2px
import kotlin.math.min

/**
 * Create by hzh on 2020/12/04.
 */
open class CircleProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseProgressBar(context, attrs, defStyleAttr) {

    private var mStartAngle = 180f // 起始位置，默认9点钟方向开始

    private var mBotStrokeWidth = 10f.dp2px(context) // 底部边宽

    private var mSecondaryStrokeWidth = 10f.dp2px(context) // 第二进度边宽

    private var mProgressStrokeWidth = 10f.dp2px(context) // 最大进度边宽

    private var mRadius = 0f

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar).run {
            mStartAngle = when (getInt(R.styleable.CircleProgressBar_startPosition, 0)) {
                0 -> 180f // 9点钟方向
                1 -> 270f // 12点钟方向
                2 -> 0f // 3点钟方向
                3 -> 90f // 6点钟方向
                else -> 180f // 其他情况默认9点钟方向
            }

            mBotStrokeWidth =
                getDimension(R.styleable.CircleProgressBar_botStrokeWidth, 10f.dp2px(context))
            mSecondaryStrokeWidth =
                getDimension(R.styleable.CircleProgressBar_secondaryStrokeWidth, 10f.dp2px(context))
            mProgressStrokeWidth =
                getDimension(R.styleable.CircleProgressBar_progressStrokeWidth, 10f.dp2px(context))

            recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val maxStrokeWidth = maxOf(mBotStrokeWidth, mSecondaryStrokeWidth, mProgressStrokeWidth)
        mRadius = (min(mWidth, mHeight) - maxStrokeWidth) / 2

        if (mRadius <= 0f) throw IllegalArgumentException("radius require bigger than zero!")
    }

    override fun createGradient(colors: IntArray, paint: Paint) {
        // 设置扫描渐变，需要用Matrix旋转到起始位置的角度
        val cx = mWidth / 2f
        val cy = mHeight / 2f
        paint.shader = SweepGradient(cx, cy, colors, null).apply {
            setLocalMatrix(Matrix().also { it.setRotate(mStartAngle, cx, cy) })
        }
    }

    override fun drawPrimary(c: Canvas, paint: Paint) {
        paint.run {
            style = Paint.Style.STROKE
            strokeWidth = mBotStrokeWidth
        }

        c.drawCircle(mWidth / 2f, mHeight / 2f, mRadius, paint)
    }

    override fun drawSecondary(c: Canvas, ratio: Float, paint: Paint) {
        paint.run {
            style = Paint.Style.STROKE
            strokeWidth = mSecondaryStrokeWidth
        }

        // 画圆弧
        val halfW = mWidth / 2f
        val halfH = mHeight / 2f
        val rect = RectF(halfW - mRadius, halfH - mRadius, halfW + mRadius, halfH + mRadius)
        c.drawArc(rect, mStartAngle, 360 * ratio, false, paint)
    }

    override fun drawProgress(c: Canvas, ratio: Float, paint: Paint) {
        paint.run {
            style = Paint.Style.STROKE
            strokeWidth = mProgressStrokeWidth
        }

        // 画圆弧
        val halfW = mWidth / 2f
        val halfH = mHeight / 2f
        val rect = RectF(halfW - mRadius, halfH - mRadius, halfW + mRadius, halfH + mRadius)
        c.drawArc(rect, mStartAngle, 360 * ratio, false, paint)
    }

    override fun drawTxt(c: Canvas, ratio: Float, text: String, paint: Paint) {
        paint.let {
            // 画文字
            c.drawText(
                text,
                (mWidth - it.measureText(text)) / 2,
                (mHeight - it.descent() - it.ascent()) / 2,
                it
            )
        }
    }
}