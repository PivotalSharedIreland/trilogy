package io.pivotal.trilogy.reporting

object TestCaseReporter {
    fun generateReport(report: TestCaseReport): String {
        return if (report.didPass) reportSuccess(report) else reportFailure(report)
    }

    private fun reportFailure(report: TestCaseReport) = "FAILED\nTotal: ${report.total}, Passed: ${report.passed}, Failed: ${report.failed}"

    private fun reportSuccess(report: TestCaseReport) = "SUCCEEDED\nTotal: ${report.total}, Passed: ${report.passed}, Failed: 0"

}