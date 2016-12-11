package io.pivotal.trilogy.reporting

import io.pivotal.trilogy.test_helpers.shouldContain
import io.pivotal.trilogy.test_helpers.shouldStartWith
import io.pivotal.trilogy.test_helpers.timesRepeat
import org.jetbrains.spek.api.Spek

class TestCaseReporterTests : Spek({

    val passedTestResult = TestResult("Passed")
    val failedTestResult = TestResult("Failed", "Error message")
    describe("no failures") {
        val report = listOf(TestCaseResult(3.timesRepeat { passedTestResult }))
        val generatedReport = TestCaseReporter.generateReport(report)

        it("should report success") {
            generatedReport shouldStartWith "SUCCEEDED"
        }

        it("should report the number of passed tests") {
            generatedReport shouldContain "Passed: 3"
        }

        it("should report no failed tests") {
            generatedReport shouldContain "Failed: 0"
        }

        it("should report total number of tests") {
            generatedReport shouldContain "Total: 3"
        }
    }

    describe("passed and failed") {
        val report = listOf(TestCaseResult(2.timesRepeat { passedTestResult } + 3.timesRepeat { failedTestResult }))
        val generatedReport = TestCaseReporter.generateReport(report)

        it("should report the number of failed tests, as well as passed tests") {
            generatedReport shouldContain "Failed: 3"
            generatedReport shouldContain "Passed: 2"
        }

        it("should report the total number of tests") {
            generatedReport shouldContain "Total: 5"
        }

    }

    describe("all failures") {
        val report = listOf(TestCaseResult(3.timesRepeat { failedTestResult }))
        val generatedReport = TestCaseReporter.generateReport(report)

        it("should report failure") {
            generatedReport shouldStartWith "FAILED"
        }

        it("should report the number of failed tests") {
            generatedReport shouldContain "Failed: 3"
        }

        it("should report 0 passed tests") {
            generatedReport shouldContain "Passed: 0"
        }

        it("should report the total number of tests") {
            generatedReport shouldContain "Total: 3"
        }
    }


})