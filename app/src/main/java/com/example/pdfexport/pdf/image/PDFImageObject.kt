package com.example.pdfexport.pdf.image

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.example.pdfexport.pdf.PDFDocumentExporter
import com.example.pdfexport.pdf.PDFObject
import kotlin.math.min

class PDFImageObject(
    val bitmap: Bitmap, val width: Int, val height: Int,
    pdfDocument: PDFDocumentExporter,
    selfConfiguration: PDFDocumentExporter.Configuration
) : PDFObject(pdfDocument, selfConfiguration) {
    override fun draw(): Int {
        //插入图片时不需要进行计算换页操作
        //if (pdfDocument.getLeftYSpace() < getMinSpace()) {
        //    pdfDocument.createNewPage()
        //}
        val w = min(width, pdfDocument.defaultConfiguration.getAvailableWidth())
        val h =
            if (width > pdfDocument.defaultConfiguration.getAvailableWidth()) {
                height * 1.0F * (pdfDocument.defaultConfiguration.getAvailableWidth() * 1.0F / width)
            } else {
                min(height, pdfDocument.getLeftYSpace())
            }

        var x = selfConfiguration.leftPadding
        //处理图片对齐操作
        if (selfConfiguration.align == Paint.Align.CENTER) {
            x += (pdfDocument.defaultConfiguration.getAvailableWidth() - w) / 2
        }
        if (selfConfiguration.align == Paint.Align.RIGHT) {
            x += pdfDocument.defaultConfiguration.getAvailableWidth() - w
        }

        //绘制图片
        pdfDocument.getCurrentPage()!!.canvas.drawBitmap(
            bitmap,
            null,
            RectF(
                x.toFloat(),
                pdfDocument.getCurrentY().toFloat(),
                (x + w).toFloat(),
                (pdfDocument.getCurrentY() + h.toInt()).toFloat()
            ),
            currentPaint.value
        )
        return h.toInt()
    }

    override fun getMinSpace(): Int {
        val w = min(width, pdfDocument.defaultConfiguration.getAvailableWidth())
        val h =
            if (width > pdfDocument.defaultConfiguration.getAvailableWidth()) {
                height * 1.0F * (pdfDocument.defaultConfiguration.getAvailableWidth() * 1.0F / width)
            } else {
                min(height, pdfDocument.getLeftYSpace())
            }

        return min(selfConfiguration.pageHeight, h.toInt())
    }
}