package io.pivotal.trilogy.reporting

import io.pivotal.trilogy.i18n.MessageCreator.getI18nMessage
import io.pivotal.trilogy.testcase.MalformedTrilogyTestCase
import io.pivotal.trilogy.testproject.TestProjectResult

object TestCaseReporter {
    fun generateReport(result: TestProjectResult): String {
        if (result.didFail or result.fatalFailure)
            return listOf("[FAIL] ${result.failureMessage}", result.fatalFailureMessage, "FAILED").filterNotNull().joinToString("\n")
        return if (result.testCaseResults.all { it.didPass } and result.malformedTestCases.isEmpty()) reportSuccess(result) else reportFailure(result)
    }

    private fun reportFailure(result: TestProjectResult): String {
        return listOf(result.failureDigest, "FAILED", result.digest).joinToString("\n")
    }

    private fun reportSuccess(result: TestProjectResult) = "SUCCEEDED\n${result.digest}"

    private val TestProjectResult.total: Int get() = this.testCaseResults.fold(this.malformedTestCases.size) { accumulated, result -> accumulated + result.total }
    private val TestProjectResult.passed: Int get() = this.testCaseResults.fold(0) { accumulated, result -> accumulated + result.passed }
    private val TestProjectResult.failed: Int get() = this.testCaseResults.fold(this.malformedTestCases.size) { accumulated, result -> accumulated + result.failed }
    private val TestProjectResult.digest: String get() = "Total: ${this.total}, Passed: ${this.passed}, Failed: ${this.failed}"
    private val TestProjectResult.failureDigest: String get() {
        return (this.testCaseResults.map { it.failureDigest } + this.malformedTestCases.map { it.failureDigest })
                .joinToString("\n")
    }
    private val MalformedTrilogyTestCase.failureDigest: String get() {
        return "[FAIL] ${this.name} - ${this.errorMessage}"
    }

    private val TestCaseResult.failureDigest: String get() {
        return this.testCaseFailure + this.failedTests.map { "[FAIL] ${this.testCaseName} - ${it.testName}:\n${it.displayMessage}" }.joinToString("\n")
    }
    private val TestResult.displayMessage: String get() = this.errorMessage!!.prependIndent("    ")

    private val TestProjectResult.fatalFailureMessage: String? get() {
        return if (this.fatalFailure) getI18nMessage("fatalFailure") else null
    }

    private val TestCaseResult.testCaseFailure: String get() {
        if (this.errorMessage == null) return ""
        return "[FAIL] ${this.testCaseName}:\n" + this.errorMessage.prependIndent("    ")
    }
}


