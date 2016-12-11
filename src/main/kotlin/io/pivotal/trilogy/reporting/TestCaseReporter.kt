package io.pivotal.trilogy.reporting

object TestCaseReporter {
    fun generateReport(result: List<TestCaseResult>): String {
        return if (result.all { it.didPass }) reportSuccess(result) else reportFailure(result)
    }

    private fun reportFailure(result: List<TestCaseResult>) = "FAILED\nTotal: ${result.total}, Passed: ${result.passed}, Failed: ${result.failed}"
    private fun reportSuccess(result: List<TestCaseResult>) = "SUCCEEDED\nTotal: ${result.total}, Passed: ${result.passed}, Failed: 0"

    private val List<TestCaseResult>.total get() = this.fold(0) { accumulated, result -> accumulated + result.total }
    private val List<TestCaseResult>.passed get() = this.fold(0) { accumulated, result -> accumulated + result.passed }
    private val List<TestCaseResult>.failed get() = this.fold(0) { accumulated, result -> accumulated + result.failed }

}