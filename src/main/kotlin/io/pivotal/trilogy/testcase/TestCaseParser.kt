package io.pivotal.trilogy.testcase

class TestCaseParser(testCase: String) : TestCaseReader {
    class InvalidTestCaseFormat(message: String?) : RuntimeException(message) {}

    init {
        if (!testCase.isValidTestCase()) throw InvalidTestCaseFormat("Unable to recognise a test case")
    }

    override fun getTestCase(): TrilogyTestCase {
        throw UnsupportedOperationException()
    }


    private fun String.isValidTestCase(): Boolean {
        return hasValidHeader() && hasValidTestCase()
    }

    private fun String.hasValidHeader() = this.contains(Regex("^# TEST CASE \\S+\n"))

    private fun String.hasValidTestCase() = this.contains(Regex("## TEST"))



}