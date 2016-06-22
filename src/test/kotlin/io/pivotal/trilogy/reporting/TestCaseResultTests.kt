package io.pivotal.trilogy.reporting

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TestCaseResultTests : Spek({
    val resultOne = TestCaseResult(1, 2)
    val resultTwo = TestCaseResult(3, 4)

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