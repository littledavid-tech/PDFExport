package com.example.pdfexport

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.pdfexport.pdf.PDFDocumentExporter
import com.example.pdfexport.pdf.table.Table
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.findViewById<Button>(R.id.btnExport).setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.pdf_img)

            val pdfDocumentExporter = PDFDocumentExporter(this)
                .addH1("Title1")
                .addImage(bitmap)
                .addH2("Title2")
                .addParagraph("豫章故郡，洪都新府。星分翼轸，地接衡庐。襟三江而带五湖，控蛮荆而引瓯越。物华天宝，龙光射牛斗之墟；人杰地灵，徐孺下陈蕃之榻。雄州雾列，俊采星驰。台隍枕夷夏之交，宾主尽东南之美。都督阎公之雅望，棨戟遥临；宇文新州之懿范，襜帷暂驻。十旬休假，胜友如云；千里逢迎，高朋满座。腾蛟起凤，孟学士之词宗；紫电青霜，王将军之武库。家君作宰，路出名区；童子何知，躬逢胜饯。(豫章故郡 一作：南昌故郡；青霜 一作：清霜)")
                .addParagraph("时维九月，序属三秋。潦水尽而寒潭清，烟光凝而暮山紫。俨骖騑于上路，访风景于崇阿。临帝子之长洲，得天人之旧馆。层峦耸翠，上出重霄；飞阁流丹，下临无地。鹤汀凫渚，穷岛屿之萦回；桂殿兰宫，即冈峦之体势。（天人 一作：仙人；层峦 一作：层台；即冈 一作：列冈；飞阁流丹 一作：飞阁翔丹")
                .addH3("Title3")
                .addTable(Table().apply {
                    addColumn("商品编号", "商品名称", "商品描述", "价格")
                    addRow(
                        "#1",
                        "商品1",
                        "商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述",
                        "$99.00"
                    )
                    addRow("#2", "商品3", "商品描述", "$999.00")
                    addRow(
                        "#1",
                        "商品2",
                        "商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述",
                        "$99.00"
                    )
                    addRow(
                        "#3",
                        "商品3",
                        "商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述",
                        "$99.00"
                    )
                    addRow(
                        "#4",
                        "商品4",
                        "商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述",
                        "$99.00"
                    )
                    addRow(
                        "#5",
                        "商品5",
                        "商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述",
                        "$99.00"
                    )
                    addRow(
                        "#6",
                        "商品6",
                        "商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述",
                        "$99.00"
                    )
                    addRow(
                        "#7",
                        "商品7",
                        "商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述 商品描述",
                        "$99.00"
                    )
                })

            val outputStream = File(filesDir, "export.pdf").outputStream()
            pdfDocumentExporter.export(outputStream)
        }
    }
}