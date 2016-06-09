package io.pivotal.trilogy.parsing

class MarkdownTable(val table: String) {
    fun getHeaders(): List<String> {
        val values = table.split("|")
        return if (values.count() > 1) listOf(values[1].trim()) else emptyList()
    }

    fun getValues(): List<List<String>> {
        val values = table.split("\n", limit = 3)
        return if (values.count() == 3) extractValues(values[2]) else emptyList()
    }

    private fun extractValues(valueTable: String): List<List<String>> {
        return valueTable.split("\n").map { it.split("|").map { it.trim() }.filter { it.count() > 0 } }
    }

}