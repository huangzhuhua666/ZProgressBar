package com.hzh.zprogressbar.base

import android.view.MotionEvent

/**
 * Create by hzh on 2/23/21.
 */
internal interface IDragHandler {

    fun onActionDown(event: MotionEvent): Boolean

    fun onActionMove(event: MotionEvent)

    fun onActionUp(event: MotionEvent)
}