package com.example.pdfexport.pdf.text

import android.util.Log
import com.example.pdfexport.getTextHeight
import com.example.pdfexport.pdf.PDFDocumentExporter
import com.example.pdfexport.pdf.PDFObject

class PDFTextObject(
    val text: String,
    pdfDocument: PDFDocumentExporter,
    selfConfiguration: PDFDocumentExporter.Configuration
) : PDFObject(pdfDocument, selfConfiguration) {
    override fun draw(): Int {

        if (this.pdfDocument.getCurrentPage() == null) {
            throw IllegalArgumentException("Cannot find pdf page.")
        }

        var x = selfConfiguration.leftPadding
        var y = currentPaint.value.getTextHeight()

        //字体的高度
        val textHeight = this.currentPaint.value.getTextHeight()

        for (c in text) {
            //先测量再绘制
            val textWidth = this.currentPaint.value.measureText(c.toString()).toInt()
            //换行操作
            if (x + textWidth >= selfConfiguration.getAvailableWidth()) {

                x = selfConfiguration.leftPadding
                y += textHeight + selfConfiguration.lineSpace
                //换页操作
                if (pdfDocument.getLeftYSpace() - y < getMinSpace()) {
                    Log.e(
                        "TAG",
                        "Text 换页 ${pdfDocument.getLeftYSpace() - y}   --  ${getMinSpace()}"
                    )
                    this.pdfDocument.createNewPage()
                    y = currentPaint.value.getTextHeight()
                }
            }

            this.pdfDocument.getCurrentPage()!!.canvas.drawText(
                c.toString(),
                x.toFloat(),
                (y + pdfDocument.getCurrentY()).toFloat(),
                this.currentPaint.value
            )
            x += textWidth + selfConfiguration.textSpace
        }

        //处理最后一行
        y += textHeight

        return y
    }

    override fun getMinSpace(): Int {
        return currentPaint.value.getTextHeight() + selfConfiguration.lineSpace
    }

}