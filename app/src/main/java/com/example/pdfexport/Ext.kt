package com.example.pdfexport

import android.content.Context
import android.graphics.Paint
import android.util.TypedValue

/**
 * 获取文本的高度
 * */
fun Paint.getTextHeight(): Int {
    return this.fontMetricsInt.let {
        it.descent - it.ascent
    }
}

fun Int.spToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        context.resources.displayMetrics
    )
        .toInt()
}