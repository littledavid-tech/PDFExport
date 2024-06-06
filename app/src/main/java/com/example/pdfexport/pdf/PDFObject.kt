package com.example.pdfexport.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument

/**
 * PDF基础绘制对象
 * */
abstract class PDFObject(
    val pdfDocument: PDFDocumentExporter,
    val selfConfiguration: PDFDocumentExporter.Configuration
) {
    protected val currentPaint = lazy { createPaint() }

    open fun createPaint(): Paint {
        return Paint().apply {
            textSize = selfConfiguration.textSize.toFloat()
            color = Color.BLACK
            isFakeBoldText = selfConfiguration.isBold
            textSkewX = if (selfConfiguration.isItalic) -0.25F else 0F
            isAntiAlias = selfConfiguration.isAntiAlias
        }
    }

    open fun performDraw(): Int {
        return draw()
    }

    abstract fun draw(): Int

    /**
     * 获取最小的空间
     * */
    abstract fun getMinSpace(): Int

}