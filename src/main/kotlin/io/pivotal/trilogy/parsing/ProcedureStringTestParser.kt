package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.i18n.MessageCreator.getI18nMessage
import io.pivotal.trilogy.parsing.exceptions.MalformedDataSection
import io.pivotal.trilogy.parsing.exceptions.MissingDataSection
import io.pivotal.trilogy.parsing.exceptions.MissingTestDescription
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.ValidProcedureTrilogyTest

class ProcedureStringTestParser(testBody: String) : BaseStringTestParser(testBody) {
    private val dataSectionHeader = "### DATA\\n"
    private val headerRow = "\\s*\\|.*?\\n"
    private val headerSeparationRow = "\\s*\\|([:-]+\\|)+\\n"
    private val valueRows = "(\\s*\\|.+?(\\n|\\Z))+"

    init {
        validate()
    }

    override fun getTest(): ValidProcedureTrilogyTest {
        return ValidProcedureTrilogyTest(parseDescription(), parseArgumentTable(), parseAssertions())
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
        if (description.isEmpty())
            throw MissingTestDescription(
                    getI18nMessage("testParser.generic.errors.missingDescription.message"),
                    getI18nMessage("testParser.generic.errors.missingDescription.testName")
            )
        return description
    }

    override fun validate() {
        super.validate()
        if (!testBody.hasDataSection()) throw MissingDataSection(getI18nMessage("testCaseParser.errors.missingDataSection"), testNameOrDefault())
        if (!testBody.hasValidDataSection()) throw MalformedDataSection(getI18nMessage("testCaseParser.errors.malformedDataSection"), testNameOrDefault())
    }

    private fun testNameOrDefault(): String {
        val testName = try {
            parseDescription()
        } catch (e: RuntimeException) {
            getI18nMessage("test.untitled")
        }
        return testName
    }

    private fun String.hasDataSection(): Boolean {
        return this.contains(Regex(dataSectionHeader))
    }


    private fun String.hasValidDataSection(): Boolean {
        return this.contains(Regex(dataSectionHeader + headerRow + headerSeparationRow + valueRows))
    }
}