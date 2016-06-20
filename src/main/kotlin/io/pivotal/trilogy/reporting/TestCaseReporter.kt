package io.pivotal.trilogy.reporting

object TestCaseReporter {
    fun generateReport(result: TestCaseResult): String {
        return if (result.didPass) reportSuccess(result) else reportFailure(result)
    }

    private fun reportFailure(result: TestCaseResult) = "FAILED\nTotal: ${result.total}, Passed: ${result.passed}, Failed: ${result.failed}"

    private fun reportSuccess(result: TestCaseResult) = "SUCCEEDED\nTotal: ${result.total}, Passed: ${result.passed}, Failed: 0"

}