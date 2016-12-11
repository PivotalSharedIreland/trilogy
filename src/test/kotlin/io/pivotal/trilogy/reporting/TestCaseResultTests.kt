package io.pivotal.trilogy.reporting

import io.pivotal.trilogy.test_helpers.timesRepeat
import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TestCaseResultTests : Spek({
    val passedTestResult = TestResult("Passed")
    val failedTestResult = TestResult("Failed", "Error message")
    val resultOne = TestCaseResult(listOf(passedTestResult) + 2.timesRepeat { failedTestResult })
    val resultTwo = TestCaseResult(3.timesRepeat { passedTestResult } + 4.timesRepeat { failedTestResult })

    it("is possible to add two test case results") {
        resultOne + resultTwo
    }

    it("adds up the results for the added results") {
        (resultOne + resultTwo).apply {
            expect(4) { passed }
            expect(6) { failed }
        }
    }
})