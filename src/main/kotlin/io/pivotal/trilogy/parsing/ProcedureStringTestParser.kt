package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.ProcedureTrilogyTest
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion

class ProcedureStringTestParser(testBody: String) : BaseStringTestParser(testBody) {
    class MissingDataSection(message: String?) : RuntimeException(message)
    private val dataSectionHeader = "### DATA\\n"
    private val headerRow = "\\s*\\|.*?\\n"
    private val headerSeparationRow = "\\s*\\|([:-]+\\|)+\\n"
    private val valueRows = "(\\s*\\|.+?(\\n|\\Z))+"

    init {
        validate()
    }

    override fun getTest(): ProcedureTrilogyTest {
        return ProcedureTrilogyTest(parseDescription(), parseArgumentTable(), parseAssertions())
    }

    private fun parseArgumentTable(): TestArgumentTable {
        val dataSection = testBody
                .replace(Regex("\\A.*?$dataSectionHeader\\s*", RegexOption.DOT_MATCHES_ALL), "")
                .replace(assertionHeaderRegex, "")
                .trim()
        val table = MarkdownTable(dataSection)
        return TestArgumentTable(table.getHeaders(), table.getValues())
    }

    private fun parseDescription(): String {
        val description = testBody.replace(testHeaderRegex, "").replace(Regex("\\s*### DATA.*", RegexOption.DOT_MATCHES_ALL), "").trim()
        if (description.isEmpty()) throw MissingDescription("Every test should have a description")
        return description
    }

    override fun validate() {
        super.validate()
        if (!testBody.hasDataSection()) throw MissingDataSection("The test is missing a data section")
    }


    private fun String.hasDataSection(): Boolean {
        return this.contains(Regex(dataSectionHeader + headerRow + headerSeparationRow + valueRows))
    }
}