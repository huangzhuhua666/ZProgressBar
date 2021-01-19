package com.hzh.zprogressbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.hzh.zprogressbar.base.BaseProgressBar
import com.hzh.zprogressbar.utils.dp2px

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

    private var mBotStrokeWidth = 0f // 底部边宽

    @ColorInt
    private var mBotStrokeColor: Int = Color.BLACK // 底部颜色

    private var mIsTxtFollowProgress = false // 字体是否跟随进度条移动

    // 字体随进度条移动时，字体显示在进图条内还是进度条外，默认在内部
    private var mTextFollowStyle: TextFollowStyle = TextFollowStyle.INNER

    // 字体摆放位置，如果设置了字体跟随，则失效默认在左边
    private var mTextAlign: TextAlign = TextAlign.LEFT

    @ColorInt
    private var mTextColorCover: Int = mTextColor // 字体被进度条覆盖部分的颜色

    private var mTextMarginHorizontal = 10f.dp2px(context) // 字体水平方向的间隔

    private val mStrokePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = mBotStrokeWidth
            color = mBotStrokeColor
        }
    }

    private val mTextCoverPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mTextColorCover
            textSize = mTextSize
            isDither = true
        }
    }

    private val mRectStroke by lazy {
        val half = mBotStrokeWidth / 2f
        RectF(0 + half, 0 + half, mWidth - half, mHeight - half)
    }

    private val mRectTotal by lazy {
        RectF(
            0 + mBotStrokeWidth,
            0 + mBotStrokeWidth,
            mWidth - mBotStrokeWidth,
            mHeight - mBotStrokeWidth
        )
    }
    private val mRectRatio by lazy { RectF() }
    private val mTextPath by lazy { Path() }
    private val mClipPath by lazy { Path() }

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.RectProgressBar).run {
            mBotCorner = getDimension(R.styleable.RectProgressBar_botCorner, 0f)
            mSecondaryCorner = getDimension(R.styleable.RectProgressBar_secondaryCorner, 0f)
            mProgressCorner = getDimension(R.styleable.RectProgressBar_progressCorner, 0f)

            mBotStrokeWidth = getDimension(R.styleable.RectProgressBar_botStrokeWidth, 0f)
            mBotStrokeColor = getColor(R.styleable.RectProgressBar_botStrokeColor, Color.BLACK)

            mIsTxtFollowProgress =
                getBoolean(R.styleable.RectProgressBar_isTxtFollowProgress, false)

            mTextFollowStyle = when (getInt(R.styleable.RectProgressBar_txtFollowType, 0)) {
                0 -> TextFollowStyle.INNER
                1 -> TextFollowStyle.OUTER
                else -> TextFollowStyle.INNER
            }

            mTextAlign = when (getInt(R.styleable.RectProgressBar_txtAlign, 0)) {
                0 -> TextAlign.LEFT
                1 -> TextAlign.CENTER
                2 -> TextAlign.RIGHT
                else -> TextAlign.LEFT
            }

            mTextColorCover = getColor(R.styleable.RectProgressBar_txtColorCover, mTextColor)
            mTextMarginHorizontal =
                getDimension(R.styleable.RectProgressBar_txtMarginHorizontal, 10f.dp2px(context))

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
        c.run {
            if (mBotStrokeWidth != 0f) drawRoundRect(mRectStroke, mBotCorner, mBotCorner, mStrokePaint)

            // 画圆角矩形
            drawRoundRect(mRectTotal, mBotCorner, mBotCorner, paint)
        }
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

    override fun drawTxt(c: Canvas, ratio: Float, text: String, paint: Paint) {
        paint.let {
            val textWidth = it.measureText(text)
            // 文字显示的基线位置
            val y = (mHeight - it.descent() - it.ascent()) / 2

            // 文字显示的x左边
            val x = if (mIsTxtFollowProgress) { // 设置了字体跟随进度移动
                when (mTextFollowStyle) {
                    TextFollowStyle.INNER -> { // 字体显示在当前进度内部
                        // 最小移动的距离
                        val minMove = textWidth + mTextMarginHorizontal * 2

                        // 当前进度宽度小于最小移动距离，则字体固定在左边不需要移动
                        if (mWidth * ratio < minMove) mTextMarginHorizontal
                        else mWidth * ratio - mTextMarginHorizontal - textWidth
                    }
                    TextFollowStyle.OUTER -> { // 字体显示在当前进度后面
                        // 字体显示在当前进度后面一点
                        val dx = mWidth * ratio + mTextMarginHorizontal
                        // 最大移动的距离
                        val maxStop = mWidth - textWidth - mTextMarginHorizontal

                        if (dx < maxStop) dx
                        else maxStop // 当字体显示的位置大于最大移动的距离，固定在右边不需要移动
                    }
                }
            } else {
                // 字体固定位置显示
                when (mTextAlign) {
                    TextAlign.LEFT -> mTextMarginHorizontal
                    TextAlign.CENTER -> (mWidth - textWidth) / 2
                    TextAlign.RIGHT -> mWidth - textWidth - mTextMarginHorizontal
                }
            }

            mTextPath.reset()
            it.getTextPath(text, 0, text.length, x, y, mTextPath)

            c.run {
                // 绘制字体的path
                drawPath(mTextPath, paint)

                // 如果字体被进度覆盖的颜色和字体本身的颜色一样，就没必要再画下去了
                if (mTextColor == mTextColorCover) return

                // 裁剪出被进度覆盖的字体的绘制区域
                val isNotEmpty = clipPath(mClipPath)
                if (!isNotEmpty) return

                // 绘制被进度覆盖的字体
                c.drawPath(mTextPath, mTextCoverPaint)
            }
        }
    }

    enum class TextAlign {

        LEFT, CENTER, RIGHT
    }

    enum class TextFollowStyle {

        INNER, OUTER
    }
}