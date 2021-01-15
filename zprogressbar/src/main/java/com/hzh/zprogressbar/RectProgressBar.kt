package com.hzh.zprogressbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.hzh.zprogressbar.base.BaseProgressBar

/**
 * Create by hzh on 2020/12/04.
 */
open class RectProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseProgressBar(context, attrs, defStyleAttr) {

    private var mBotCorner = 0f // 底部的圆角
    private var mSecondaryCorner = 0f // 第二进度的圆角
    private var mProgressCorner = 0f // 当前进度的圆角

    private val mRectTotal by lazy { RectF(0f, 0f, mWidth.toFloat(), mHeight.toFloat()) }
    private val mRectRatio by lazy { RectF() }
    private val mClipPath by lazy { Path() }

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.RectProgressBar).run {
            mBotCorner = getDimension(R.styleable.RectProgressBar_botCorner, 0f)
            mSecondaryCorner = getDimension(R.styleable.RectProgressBar_secondaryCorner, 0f)
            mProgressCorner = getDimension(R.styleable.RectProgressBar_progressCorner, 0f)

            recycle()
        }
    }

    override fun createGradient(colors: IntArray, paint: Paint) {
        // 设置线性渐变色，目前是从左到右
        paint.shader = LinearGradient(
            0f, 0f,
            mWidth.toFloat(), 0f,
            colors, null,
            Shader.TileMode.CLAMP
        )
    }

    override fun drawPrimary(c: Canvas, paint: Paint) {
        // 画圆角矩形
        c.drawRoundRect(mRectTotal, mBotCorner, mBotCorner, paint)
    }

    override fun drawSecondary(c: Canvas, ratio: Float, paint: Paint) {
        c.run {
            save()

            // 设置第二进度的圆角矩形裁剪区域
            mRectRatio.set(0f, 0f, mWidth * ratio, mHeight.toFloat())
            // 进度满了之后，右边的圆角要以底部的圆角为准，否则会出现奇怪的画面
            val endCorner = if (ratio == 1f) mBotCorner else mSecondaryCorner
            mClipPath.reset()
            mClipPath.addRoundRect(
                mRectRatio,
                floatArrayOf(
                    mBotCorner, mBotCorner, endCorner, endCorner,
                    endCorner, endCorner, mBotCorner, mBotCorner,
                ),
                Path.Direction.CW
            )
            clipPath(mClipPath)

            drawRoundRect(mRectTotal, mSecondaryCorner, mSecondaryCorner, paint)
            restore()
        }
    }

    override fun drawProgress(c: Canvas, ratio: Float, paint: Paint) {
        c.run {
            save()

            // 设置第二进度的圆角矩形裁剪区域
            mRectRatio.set(0f, 0f, mWidth * ratio, mHeight.toFloat())
            // 进度满了之后，右边的圆角要以底部的圆角为准，否则会出现奇怪的画面
            val endCorner = if (ratio == 1f) mBotCorner else mProgressCorner
            mClipPath.reset()
            mClipPath.addRoundRect(
                mRectRatio,
                floatArrayOf(
                    mBotCorner, mBotCorner, endCorner, endCorner,
                    endCorner, endCorner, mBotCorner, mBotCorner,
                ),
                Path.Direction.CW
            )
            clipPath(mClipPath)

            drawRoundRect(mRectTotal, mProgressCorner, mProgressCorner, paint)
            restore()
        }
    }
}