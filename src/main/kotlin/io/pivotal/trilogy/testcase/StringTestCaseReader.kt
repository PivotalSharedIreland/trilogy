package io.pivotal.trilogy.testcase

class StringTestCaseReader(val testCase: String) : TestCaseReader {

    class InvalidTestCaseFormat(message: String?) : RuntimeException(message) {}
    class MissingDescription(message: String?) : RuntimeException(message) {}
    class MissingFunctionName(message: String?) : RuntimeException(message) {}

    private val testCaseHeaderRegex = Regex("^# TEST CASE (\\S+)\\s+")

    init {
        validate()
    }

    private fun validate() {
        if (!testCase.isValidTestCase()) throw InvalidTestCaseFormat("Unable to recognise a test case")
    }

    override fun getTestCase(): TrilogyTestCase {
        return parse()
    }

    private fun parse(): TrilogyTestCase {
        return TrilogyTestCase(parseFunctionName(), parseDescription(), emptyList())
    }

    private fun parseDescription(): String {
        val description = testCase.replace(testCaseHeaderRegex, "").replace(Regex("\\s*## TEST.*", RegexOption.DOT_MATCHES_ALL), "").trim()
        if (description.isEmpty()) throw MissingDescription("Every test case must have a description")
        return description
    }

    private fun parseFunctionName(): String {
        val functionName = testCaseHeaderRegex.find(testCase)!!.groupValues[1].trim()
        if (functionName.isEmpty()) throw MissingFunctionName("A test case should specify a function name for testing")
        return functionName
    }

    private fun String.isValidTestCase(): Boolean {
        return hasValidHeader() && hasValidTest()
    }

    private fun String.hasValidHeader() = this.contains(testCaseHeaderRegex)

    private fun String.hasValidTest() = this.contains(Regex("## TEST"))

}