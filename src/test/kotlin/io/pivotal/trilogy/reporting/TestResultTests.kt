package io.pivotal.trilogy.reporting

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TestResultTests : Spek({
    it("determines a failed test") {
        val failedResult = TestResult("", "Error message")
        expect(true) { failedResult.hasFailed }
        expect(false) { failedResult.hasSucceeded }
    }

    it("determines a successful test") {
        val successfulResult = TestResult("", null)
        expect(false) { successfulResult.hasFailed }
        expect(true) { successfulResult.hasSucceeded }
    }
})