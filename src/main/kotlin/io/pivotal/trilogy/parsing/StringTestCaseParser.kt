package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.TrilogyTest
import io.pivotal.trilogy.testcase.TrilogyTestCase

class StringTestCaseParser(val testCaseBody: String) : TestCaseParser {

    class InvalidTestCaseFormat(message: String?) : RuntimeException(message) {}
    class MissingDescription(message: String?) : RuntimeException(message) {}
    class MissingFunctionName(message: String?) : RuntimeException(message) {}

    private val testCaseHeaderRegex = Regex("^# TEST CASE (\\S*)\\s+")

    init {
        validate()
    }

    private fun validate() {
        if (!testCaseBody.isValidTestCase()) throw InvalidTestCaseFormat("Unable to recognise a test case")
    }

    override fun getTestCase(): TrilogyTestCase {
        return parse()
    }

    private fun parse(): TrilogyTestCase {
        return TrilogyTestCase(parseFunctionName(), parseDescription(), parseTests())
    }

    private fun parseDescription(): String {
        val description = testCaseBody.replace(testCaseHeaderRegex, "").replace(Regex("\\s*## TEST.*", RegexOption.DOT_MATCHES_ALL), "").trim()
        if (description.isEmpty()) throw MissingDescription("Every test case must have a description")
        return description
    }

    private fun parseFunctionName(): String {
        val functionName = testCaseHeaderRegex.find(testCaseBody)!!.groupValues[1].trim()
        if (functionName.isEmpty()) throw MissingFunctionName("A test case should specify a function name for testing")
        return functionName
    }

    private fun parseTests(): List<TrilogyTest> {
        val individualTestSections = testCaseBody.split("## TEST").drop(1).map { "## TEST$it".trim() }
        return individualTestSections.map { StringTestParser(it).getTest() }
    }

    private fun String.isValidTestCase(): Boolean {
        return hasValidHeader() && hasValidTest()
    }

    private fun String.hasValidHeader() = this.contains(testCaseHeaderRegex)

    private fun String.hasValidTest() = this.contains(Regex("## TEST"))

}