package io.pivotal.trilogy.reporting

import io.pivotal.trilogy.test_helpers.shouldContain
import io.pivotal.trilogy.test_helpers.shouldStartWith
import io.pivotal.trilogy.test_helpers.timesRepeat
import io.pivotal.trilogy.testproject.TestProjectResult
import org.jetbrains.spek.api.Spek

class TestCaseReporterTests : Spek({

    val passedTestResult = TestResult("Passed")
    val failedTestResult = TestResult("Failed test name", "Multi\nline\nerror\nmessage")
    describe("no failures") {
        val report = listOf(TestCaseResult("", 3.timesRepeat { passedTestResult }))
        val generatedReport = TestCaseReporter.generateReport(TestProjectResult(report))

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
        val report = listOf(TestCaseResult("Nirvana or zion", 2.timesRepeat { passedTestResult } + 3.timesRepeat { failedTestResult }))
        val generatedReport = TestCaseReporter.generateReport(TestProjectResult(report))

        it("should report the number of failed tests, as well as passed tests") {
            generatedReport shouldContain "Failed: 3"
            generatedReport shouldContain "Passed: 2"
        }

        it("should report the total number of tests") {
            generatedReport shouldContain "Total: 5"
        }

        it("should report every test failure") {
            generatedReport shouldContain "[FAIL] Nirvana or zion - Failed test name:\n    Multi\n    line\n    error\n    message"
        }

    }

    describe("all failures") {
        val report = TestProjectResult(listOf(TestCaseResult("", 3.timesRepeat { failedTestResult })))
        val generatedReport = TestCaseReporter.generateReport(report)

        it("should report failure") {
            generatedReport shouldContain "FAILED"
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

    describe("test case failures") {
        it("should include test case failures in the report") {
            val result = listOf(TestCaseResult("Mad test", errorMessage = "I can haz a panda"))
            val report = TestCaseReporter.generateReport(TestProjectResult(result))

            report shouldContain "FAILED"

            report shouldContain "[FAIL] Mad test:\n    I can haz a panda"
        }
    }

    describe("fatal failures") {
        it("should append the [STOP] message when a fatal failure is encountered") {
            val result = listOf(TestCaseResult("Odd travel", errorMessage = "Walnut combines greatly with chopped steak"))
            val report = TestCaseReporter.generateReport(TestProjectResult(result, fatalFailure = true))

            report shouldContain "\n[STOP] Execution aborted - the database may be in an inconsistent state\n"
        }
    }


})