package com.example.pdfexport.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint.Align
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.util.Log
import androidx.core.graphics.toColor
import com.example.pdfexport.pdf.image.PDFImageObject
import com.example.pdfexport.pdf.table.PDFTableObject
import com.example.pdfexport.pdf.table.Table
import com.example.pdfexport.pdf.text.PDFTextObject
import java.io.OutputStream
import kotlin.math.max

class PDFDocumentExporter(
    val context: Context,
    val defaultConfiguration: Configuration = Configuration()
) {
    val pdfDoc = PdfDocument()
    var pageNum = 0
    var pageList = mutableListOf<Page>()
    private val pdfObjectList = mutableListOf<PDFObject>()

    /**
     * 当前的Y轴
     * */
    private var currentY = defaultConfiguration.topPadding

    /**
     * 获取剩余空间
     * */
    fun getLeftYSpace(configuration: Configuration = defaultConfiguration): Int {
        return defaultConfiguration.pageHeight - currentY - max(
            configuration.bottomPadding,
            defaultConfiguration.bottomPadding
        )
    }

    fun getCurrentY(): Int {
        return currentY
    }

    /**
     * 新建一个PDF页面
     * */
    fun createNewPage(): Page {
        if (this.pageList.any()) {
            val lastPage = this.pageList.last()
            pdfDoc.finishPage(lastPage)
        }

        pageNum++

        val pageInfo = PdfDocument.PageInfo.Builder(
            defaultConfiguration.pageWidth,
            defaultConfiguration.pageHeight,
            pageNum
        ).create()

        val page = pdfDoc.startPage(pageInfo)
        this.pageList.add(page)

        this.currentY = defaultConfiguration.topPadding


        return page
    }

    /**
     * 获取当前页
     * */
    fun getCurrentPage(): Page? {
        return this.pageList.lastOrNull()
    }

    /**
     * 添加一级标题
     * */
    fun addH1(title: String, config: Configuration? = null): PDFDocumentExporter {
        val def = defaultConfiguration.copy(textSize = defaultConfiguration.firstTitleSize)
        this.pdfObjectList.add(PDFTextObject(title, this, config ?: def))
        return this
    }

    /**
     * 添加二级标题
     * */
    fun addH2(title: String, config: Configuration? = null): PDFDocumentExporter {
        val def = defaultConfiguration.copy(textSize = defaultConfiguration.secondTitleSize)
        this.pdfObjectList.add(PDFTextObject(title, this, config ?: def))
        return this
    }

    /**
     * 添加二级标题
     * */
    fun addH3(title: String, config: Configuration? = null): PDFDocumentExporter {
        val def = defaultConfiguration.copy(textSize = defaultConfiguration.thirdTitleSize)
        this.pdfObjectList.add(PDFTextObject(title, this, config ?: def))
        return this
    }

    /**
     * 添加段落
     * */
    fun addParagraph(content: String, config: Configuration? = null): PDFDocumentExporter {
        this.pdfObjectList.add(PDFTextObject(content, this, config ?: defaultConfiguration))
        return this
    }

    /**
     * 添加图片
     * @param width 图片的宽度，为<=0则使用原始宽度
     * @param height 图片的高度,为<=0则使用原始高度
     * */
    fun addImage(
        bitmap: Bitmap,
        width: Int = -1,
        height: Int = -1,
        config: Configuration? = null
    ): PDFDocumentExporter {

        val def = defaultConfiguration.copy(align = Align.CENTER)

        val w = if (width <= 0) {
            bitmap.width
        } else {
            width
        }

        val h = if (height <= 0) {
            bitmap.height
        } else {
            height
        }

        this.pdfObjectList.add(
            PDFImageObject(
                bitmap,
                w,
                h,
                this,
                config ?: def
            )
        )
        return this
    }


    fun addTable(table: Table, config: Configuration? = null): PDFDocumentExporter {
        val def = defaultConfiguration.copy(leftPadding = 5, rightPadding = 5)

        this.pdfObjectList.add(PDFTableObject(table, this, config ?: def))
        return this
    }

    /**
     * 创建PDF文档
     * */
    fun export(stream: OutputStream) {
        if (!this.pageList.any()) {
            this.createNewPage()
        }

        this.pdfObjectList.forEach {
            if (this.getLeftYSpace() < it.getMinSpace()) {
                Log.e(
                    "TAG",
                    "LeftSpace:${this.getLeftYSpace()}  MinSpace:${it.getMinSpace()}"
                )
                this.createNewPage()
            }
            val y = it.performDraw()
            this.currentY += y + defaultConfiguration.paragraphSpace
            Log.e("TAG", "currentY:" + currentY)
        }

        pdfDoc.finishPage(this.pageList.last())
        pdfDoc.writeTo(stream)
    }

    data class Configuration(
        /**
         * 页面宽度
         * */
        val pageWidth: Int = 595,
        /**
         * 页面高度
         * */
        val pageHeight: Int = 842,
        /**
         * 左间距
         * */
        val leftPadding: Int = 20,
        /**
         * 上间距
         * */
        val topPadding: Int = 50,
        /**
         * 右间距
         * */
        val rightPadding: Int = 20,
        /**
         * 下间距
         * */
        val bottomPadding: Int = 50,
        /**
         * 行间距
         * */
        val lineSpace: Int = 5,
        /**
         * 段落间距
         * */
        val paragraphSpace: Int = 20,
        /**
         * 字间距
         * */
        val textSpace: Int = 2,
        /**
         * 字体大小
         * */
        val textSize: Int = 16,
        /**
         * 一级标题的字体大小
         * */
        val firstTitleSize: Int = 30,
        /**
         * 二级标题字体大小
         * */
        val secondTitleSize: Int = 30,
        /**
         * 三级标题字体大小
         * */
        val thirdTitleSize: Int = 30,
        /**
         * 对齐方式
         * */
        val align: Align = Align.LEFT,
        /**
         * 字体是否粗体
         * */
        val isBold: Boolean = false,
        /**
         * 字体是否斜体
         * */
        val isItalic: Boolean = false,
        /**
         * 字体颜色
         * */
        val textColor: Color = Color.BLACK.toColor(),
        /**
         * 边框颜色
         * */
        val tableBorderColor: Color = Color.BLACK.toColor(),
        /**
         * 边框粗细
         * */
        val tableBorderWeight: Int = 1,
        val isAntiAlias: Boolean = true
    ) {
        fun getAvailableWidth(): Int {
            return this.pageWidth - leftPadding - rightPadding
        }
    }
}