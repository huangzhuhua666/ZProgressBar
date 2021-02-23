package com.hzh.zprogressbar.drag

import android.view.MotionEvent
import com.hzh.zprogressbar.RectProgressBar
import com.hzh.zprogressbar.base.IDragHandler

/**
 * Create by hzh on 2/23/21.
 */
internal class RectDragHandler(private val target: RectProgressBar) : IDragHandler {

    override fun onActionDown(
        event: MotionEvent
    ): Boolean = if (target.getViewRegion().contains(event.x.toInt(), event.y.toInt())) {
        target.parent.requestDisallowInterceptTouchEvent(true)

        handleProgressChange(event)

        true
    } else false

    override fun onActionMove(event: MotionEvent) {
        handleProgressChange(event)
    }

    override fun onActionUp(event: MotionEvent) {
        target.performClick()
    }

    private fun handleProgressChange(event: MotionEvent) {
        target.run {
            val rawX = event.rawX

            setProgressByTouch(
                when {
                    rawX < left -> 0
                    rawX > right -> getMax()
                    else -> ((rawX - left) / measuredWidth * getMax()).toInt()
                }
            )
        }
    }
}