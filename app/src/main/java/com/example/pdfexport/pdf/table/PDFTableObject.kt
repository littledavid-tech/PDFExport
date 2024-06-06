package com.example.pdfexport.pdf.table

import android.graphics.Paint
import android.graphics.RectF
import android.text.StaticLayout
import android.text.TextPaint
import com.example.pdfexport.getTextHeight
import com.example.pdfexport.pdf.PDFDocumentExporter
import com.example.pdfexport.pdf.PDFObject
import java.awt.font.TextAttribute

class PDFTableObject(
    val table: Table,
    pdfDocument: PDFDocumentExporter,
    selfConfiguration: PDFDocumentExporter.Configuration
) : PDFObject(pdfDocument, selfConfiguration) {
    /**
     * 用于绘制边框的画笔
     * */
    private val borderPaint = Paint().apply {
        color = selfConfiguration.tableBorderColor.toArgb()
        strokeWidth = selfConfiguration.tableBorderWeight.toFloat()
        style = Paint.Style.STROKE
        isAntiAlias = selfConfiguration.isAntiAlias
    }

    /**
     * 绘制表格
     * */
    override fun draw(): Int {
        var x = pdfDocument.defaultConfiguration.leftPadding
        var y = 0
        for ((index, row) in this.table.table.withIndex()) {
            val rowHeight = getMaxHeightCellOfRow(row)

            //当无法存下当前行数据时，新建一页文档
            if (this.pdfDocument.getLeftYSpace() - y - rowHeight < getMinSpace()) {
                pdfDocument.createNewPage()
                y = 0
            }

            drawRow(x, pdfDocument.getCurrentY() + y, rowHeight, row)
            y += rowHeight
        }
        return y
    }

    /**
     * 绘制行
     * */
    private fun drawRow(x: Int, y: Int, rowHeight: Int, rowList: List<String>) {
        var currentX = x
        rowList.forEach {
            drawCell(currentX, y, rowHeight, it)
            currentX += getColumnWidth()
        }
    }

    /**
     * 绘制单元格
     * */
    private fun drawCell(x: Int, y: Int, cellHeight: Int, text: String) {
        drawBorder(x, y, cellHeight)
        drawCellContent(x, y, text)
    }

    /**
     * 绘制单元格文本
     * */
    private fun drawCellContent(x: Int, y: Int, text: String) {
        var currentX = selfConfiguration.leftPadding
        var currentY = y + currentPaint.value.getTextHeight()
        var lastIndex = 0

        val textPaint = TextPaint()
        textPaint.textSize = this.currentPaint.value.textSize
        textPaint.color = this.currentPaint.value.color
        textPaint.isFakeBoldText = this.currentPaint.value.isFakeBoldText
        textPaint.textSkewX = currentPaint.value.textSkewX
        textPaint.isAntiAlias = currentPaint.value.isAntiAlias

        val layout = StaticLayout.Builder.obtain(
            text,
            0,
            text.length,
            textPaint,
            getColumnContentWidth()
        ).build()
        pdfDocument.getCurrentPage()!!.canvas.save()
        pdfDocument.getCurrentPage()!!.canvas.translate(x + currentX.toFloat(), currentY.toFloat())
        layout.draw(pdfDocument.getCurrentPage()!!.canvas)
        pdfDocument.getCurrentPage()!!.canvas.restore()
//        for (i in 0..<(text.length - 1)) {
//            val subStr = text.slice(lastIndex..i - lastIndex)
//            if (currentPaint.value.measureText(subStr) > getColumnContentWidth()) {
//                currentY += currentPaint.value.getTextHeight() + selfConfiguration.lineSpace
//                lastIndex = i
//            }
//            pdfDocument.getCurrentPage()!!.canvas.drawText(
//                subStr,
//                (x + currentX).toFloat(),
//                y.toFloat(),
//                currentPaint.value
//            )
//        }
//        for (c in text) {
//            //循环绘制
////            while (lastIndex < text.length) {
////                for (i in lastIndex..<text.length - 1) {
////                    //换行操作
////                    if (currentPaint.value.measureText(text.slice(lastIndex..i + 1)) > getColumnContentWidth()) {
////                        pdfDocument.getCurrentPage()!!.canvas.drawText(
////                            text.slice(lastIndex..i),
////                            x.toFloat(),
////                            y.toFloat(),
////                            currentPaint.value
////                        )
////                    }
////                }
////                lastIndex++
////            }
//
//
////            val textWidth = currentPaint.value.measureText(c.toString()).toInt()
//            //换行
////            if (currentX + textWidth + selfConfiguration.textSpace > getColumnContentWidth()) {
////                currentX = selfConfiguration.leftPadding
////                currentY += currentPaint.value.getTextHeight() + selfConfiguration.lineSpace
////            }
////            currentX += textWidth + selfConfiguration.textSpace
////
////            pdfDocument.getCurrentPage()!!.canvas.drawText(
////                c.toString(),
////                (x + currentX).toFloat(),
////                currentY.toFloat(),
////                currentPaint.value
////            )
//        }
    }

    private fun drawBorder(x: Int, y: Int, height: Int) {

        pdfDocument.getCurrentPage()!!.canvas.drawRect(
            RectF(
                x.toFloat(),
                y.toFloat(),
                (x + getColumnWidth()).toFloat(),
                (y + height).toFloat()
            ), borderPaint
        )
    }

    /**
     * 获取指定行中最高的高度
     * */
    private fun getMaxHeightCellOfRow(rowList: List<String>): Int {
        return rowList.map { measureTextHeightOfCell(it) }.max() + (selfConfiguration.lineSpace * 2)
    }

    /**
     * 测量单元格中换行后的文本单元格的高度
     * */
    private fun measureTextHeightOfCell(str: String): Int {
        var height = currentPaint.value.getTextHeight()
        var x = 0
        for (c in str) {
            val textWidth = currentPaint.value.measureText(c.toString()).toInt()
            //换行操作
            if (x + textWidth + selfConfiguration.textSpace > getColumnWidth()) {
                x = 0
                height += currentPaint.value.getTextHeight() + selfConfiguration.lineSpace
            }
            x += textWidth + selfConfiguration.textSpace
        }
        height += currentPaint.value.getTextHeight() + selfConfiguration.lineSpace
        return height
    }

    /**
     * 获取每个列可以放的内容的平均宽度
     * */
    private fun getColumnContentWidth(): Int {
        return getColumnWidth() - selfConfiguration.leftPadding - selfConfiguration.rightPadding
    }

    /**
     * 获取每个列的平均宽度
     * */
    private fun getColumnWidth(): Int {
        return (pdfDocument.defaultConfiguration.getAvailableWidth()) / table.table[0].size
    }

    override fun getMinSpace(): Int {
        return currentPaint.value.getTextHeight() + (selfConfiguration.lineSpace * 2)
    }


}