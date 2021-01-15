package com.hzh.zprogressbar.utils

import android.content.Context
import android.util.TypedValue

/**
 * Create by hzh on 2020/12/24.
 */
internal fun Float.dp2px(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
)

internal fun Float.sp2px(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    context.resources.displayMetrics
)
