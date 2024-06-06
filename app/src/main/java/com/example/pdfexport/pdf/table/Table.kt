package com.example.pdfexport.pdf.table

class Table {
    val table = mutableListOf<MutableList<String>>()

    /**
     * 添加列
     * */
    fun addColumn(vararg columnContent: String) {
        if (!columnContent.any()) {
            throw IllegalArgumentException("Column count cannot be zero!")
        }
        if (table.any()) {
            throw IllegalArgumentException("Table has existed one column row!")
        }

        table.add(columnContent.toMutableList())
    }

    fun addRow(vararg rowContent: String) {
        if (!this.table.any()) {
            throw IllegalArgumentException("Column no existed!")
        }
        if (!rowContent.any()) {
            throw IllegalArgumentException("Row content cannot be zero!")
        }
        table.add(rowContent.toMutableList())
    }
}