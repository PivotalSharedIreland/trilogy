package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTest

class StringTestParser(val testBody: String) : TestParser {
    class InvalidTestFormat(message: String?) : RuntimeException(message) {}
    class MissingDataSection(message: String?) : RuntimeException(message) {}
    class MissingDescription(message: String?) : RuntimeException(message) {}

    private val dataSectionHeader = "### DATA\\n"
    private val headerRow = "\\s*\\|.*?\\n"
    private val headerSeparationRow = "\\s*\\|([:-]+\\|)+\\n"
    private val valueRows = "(\\s*\\|.+?(\\n|\\Z))+"

    private val testHeaderRegex = Regex("\\A\\s*## TEST\\s*")
    private val assertionHeaderRegex = Regex("### ASSERTIONS\\s+(.*)", RegexOption.DOT_MATCHES_ALL)

    override fun getTest(): TrilogyTest {
        return parse()
    }

    init {
        validate()
    }

    private fun parse(): TrilogyTest {
        return TrilogyTest(parseDescription(), parseArgumentTable(), parseAssertions())
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

    private fun parseAssertions(): List<TrilogyAssertion> {
        val assertionsSection = assertionHeaderRegex.find(testBody)?.groups?.get(1)?.value ?: return emptyList()

        return Regex("#### SQL\\s*\\n(.+?)```(.+?)```", RegexOption.DOT_MATCHES_ALL)
                .findAll(assertionsSection)
                .map { match -> TrilogyAssertion(match.groupValues[1].trim(), match.groupValues[2].trim()) }
                .toList()
    }

    private fun validate() {
        if (!testBody.hasValidFormat()) throw InvalidTestFormat("Unable to recognise the test")
        if (!testBody.hasDataSection()) throw MissingDataSection("The test is missing a data section")
    }


    private fun String.hasValidFormat(): Boolean = this.contains(testHeaderRegex)

    private fun String.hasDataSection(): Boolean {
        return this.contains(Regex(dataSectionHeader + headerRow + headerSeparationRow + valueRows))
    }
}