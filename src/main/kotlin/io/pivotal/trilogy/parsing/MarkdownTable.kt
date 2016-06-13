package io.pivotal.trilogy.parsing

class MarkdownTable(val table: String) {

    fun getHeaders(): List<String> {
        val headerLine = table.split("\n", limit = 2)[0]
        val headers = extractHeaders(headerLine)
        return if (headers.count() > 0) headers else emptyList()
    }

    fun getValues(): List<List<String>> {
        val values = table.split("\n", limit = 3)
        return if (values.count() == 3) extractValues(values[2]) else emptyList()
    }

    private fun extractHeaders(header: String): List<String> {
        return header.split("|").map { it.trim() }.filter { !it.equals("") }
    }

    private fun extractValues(valueTable: String): List<List<String>> {
        return valueTable.split("\n").map { it.split("|").map { it.trim() }.drop(1).dropLast(1) }
    }

}