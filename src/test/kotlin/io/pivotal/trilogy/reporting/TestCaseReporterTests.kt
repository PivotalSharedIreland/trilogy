package io.pivotal.trilogy.reporting

import org.jetbrains.spek.api.Spek
import kotlin.test.assertTrue

class TestCaseReporterTests : Spek({

    describe("no failures") {
        val report = TestCaseResult(passed = 3)
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
        val report = TestCaseResult(passed = 2, failed = 3)
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
        val report = TestCaseResult(failed = 3)
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

private infix fun String.shouldContain(other: String) = assertTrue(this.contains(other), "Expected '$this' to contain '$other'")
private infix fun String.shouldStartWith(other: String) = assertTrue(this.startsWith(other), "Expected '$this' to start with '$other'")