package io.pivotal.trilogy.reporting

object TestCaseReporter {
    fun generateReport(result: List<TestCaseResult>): String {
        return if (result.all { it.didPass }) reportSuccess(result) else reportFailure(result)
    }

    private fun reportFailure(result: List<TestCaseResult>) = listOf(result.failures, "FAILED", result.digest).joinToString("\n")
    private fun reportSuccess(result: List<TestCaseResult>) = "SUCCEEDED\n${result.digest}"

    private val List<TestCaseResult>.total get() = this.fold(0) { accumulated, result -> accumulated + result.total }
    private val List<TestCaseResult>.passed get() = this.fold(0) { accumulated, result -> accumulated + result.passed }
    private val List<TestCaseResult>.failed get() = this.fold(0) { accumulated, result -> accumulated + result.failed }
    private val List<TestCaseResult>.digest get() = "Total: ${this.total}, Passed: ${this.passed}, Failed: ${this.failed}"
    private val List<TestCaseResult>.failures get() = this.map { it.failureDigest }.joinToString("\n")

    private val TestCaseResult.failureDigest: String get() {
        return this.failedTests.map { "[FAIL] ${this.testCaseName} - ${it.testName}: ${it.errorMessage}" }.joinToString("\n")
    }
}