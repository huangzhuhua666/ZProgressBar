package com.hzh.zprogressbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.core.graphics.toRect
import com.hzh.zprogressbar.base.BaseProgressBar
import com.hzh.zprogressbar.drag.RectDragHandler
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
    private var mSecondaryCorner = 0f // 副进度的圆角
    private var mProgressCorner = 0f // 当前进度的圆角

    private var mBotStrokeWidth = 0f // 底部边宽

    @ColorInt
    private var mBotStrokeColor: Int = Color.BLACK // 底部颜色

    private var mIsTxtFollowProgress = false // 字体是否跟随进度条移动

    // 字体随进度条移动时，字体显示在进度条内还是进度条外，默认在内部
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
        RectF(half, half, mWidth - half, mHeight - half)
    }

    private val mRectTotal by lazy {
        RectF(
            mBotStrokeWidth,
            mBotStrokeWidth,
            mWidth - mBotStrokeWidth,
            mHeight - mBotStrokeWidth
        )
    }
    private val mRectRatio by lazy { RectF() }
    private val mTextPath by lazy { Path() }
    private val mClipPath by lazy { Path() }

    private var mIsDraggable = false // 能否拖拽改变进度，默认不能
    private val mTouchHandler by lazy { RectDragHandler(this) }

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

            mIsDraggable = getBoolean(R.styleable.RectProgressBar_isDraggable, false)

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

            // 设置副进度的圆角矩形裁剪区域
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

            drawRoundRect(mRectTotal, mBotCorner, mBotCorner, paint)
            restore()
        }
    }

    override fun drawProgress(c: Canvas, ratio: Float, paint: Paint) {
        c.run {
            save()

            // 设置副进度的圆角矩形裁剪区域
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

            drawRoundRect(mRectTotal, mBotCorner, mBotCorner, paint)
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

    /**
     * 设置底部进度条的圆角
     */
    @Synchronized
    fun setBotCorner(corner: Int) {
        val dpCorner = corner.toFloat().dp2px(context)

        if (mBotCorner == dpCorner) return
        mBotCorner = dpCorner

        invalidate()
    }

    /**
     * 设置副进度条的圆角
     */
    @Synchronized
    fun setSecondaryCorner(corner: Int) {
        val dpCorner = corner.toFloat().dp2px(context)

        if (mSecondaryCorner == dpCorner) return
        mSecondaryCorner = dpCorner

        invalidate()
    }

    /**
     * 设置进度条的圆角
     */
    @Synchronized
    fun setProgressCorner(corner: Int) {
        val dpCorner = corner.toFloat().dp2px(context)

        if (mProgressCorner == dpCorner) return
        mProgressCorner = dpCorner

        invalidate()
    }

    /**
     * 设置描边宽度
     */
    @Synchronized
    fun setBotStrokeWidth(width: Int) {
        val dpWidth = width.toFloat().dp2px(context)

        if (mBotStrokeWidth == dpWidth) return
        mBotStrokeWidth = dpWidth
        mStrokePaint.strokeWidth = mBotStrokeWidth

        if (mWidth != 0 && mHeight != 0) {
            val half = mBotStrokeWidth / 2f
            mRectStroke.set(half, half, mWidth - half, mHeight - half)

            mRectTotal.set(
                mBotStrokeWidth,
                mBotStrokeWidth,
                mWidth - mBotStrokeWidth,
                mHeight - mBotStrokeWidth
            )
        }

        invalidate()
    }

    /**
     * 设置描边颜色
     */
    @Synchronized
    fun setBotStrokeColor(@ColorInt color: Int) {
        if (mBotStrokeColor == color) return

        mBotStrokeColor = color
        mStrokePaint.color = mBotStrokeColor

        invalidate()
    }

    /**
     * 设置字体是否跟随当前进度条移动
     */
    @Synchronized
    fun setIsTxtFollowProgress(isFollow: Boolean) {
        if (mIsTxtFollowProgress == isFollow) return

        mIsTxtFollowProgress = isFollow

        invalidate()
    }

    /**
     * 设置字体随进度条移动时，字体显示在进度条内还是进度条外
     */
    @Synchronized
    fun setTextFollowStyle(style: TextFollowStyle) {
        if (mTextFollowStyle == style) return

        mTextFollowStyle = style

        invalidate()
    }

    /**
     * 设置字体摆放位置，如果设置了字体跟随，则失效默认在左边
     */
    @Synchronized
    fun setTextAlign(align: TextAlign) {
        if (mTextAlign == align) return

        mTextAlign = align

        invalidate()
    }

    /**
     * 设置字体被进度条覆盖部分的颜色
     */
    @Synchronized
    fun setTextColorCover(@ColorInt color: Int) {
        if (mTextColorCover == color) return

        mTextColorCover = color
        mTextCoverPaint.color = mTextColorCover

        invalidate()
    }

    /**
     * 设置字体水平方向的间隔
     */
    @Synchronized
    fun setTextMarginHorizontal(margin: Int) {
        val dpMargin = margin.toFloat().dp2px(context)

        if (mTextMarginHorizontal == dpMargin) return
        mTextMarginHorizontal = dpMargin

        invalidate()
    }

    /**
     * 设置能否通过拖拽改变进度
     */
    @Synchronized
    fun setDraggable(isDraggable: Boolean) {
        if (mIsDraggable == isDraggable) return

        mIsDraggable = isDraggable
    }

    enum class TextAlign {

        LEFT, CENTER, RIGHT
    }

    enum class TextFollowStyle {

        INNER, OUTER
    }

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!mIsDraggable) return super.onTouchEvent(event)

        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> return mTouchHandler.onActionDown(event)
            MotionEvent.ACTION_MOVE -> mTouchHandler.onActionMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mTouchHandler.onActionUp(event)
        }

        return super.onTouchEvent(event)
    }

    internal fun getViewRegion() = Region(mRectTotal.toRect())
}