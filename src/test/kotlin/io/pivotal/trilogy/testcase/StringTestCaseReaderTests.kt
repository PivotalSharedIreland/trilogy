package io.pivotal.trilogy.testcase

import io.pivotal.trilogy.ResourceHelper
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect

class StringTestCaseReaderTests : Spek ({


    describe("degenerate") {
        val validTestCase = ResourceHelper.getTestCaseByName("degenerate")

        it("succeeds with a valid test case") {
            StringTestCaseReader(validTestCase)
        }

        it("gets a valid test case name") {
            val testCaseParser = StringTestCaseReader(validTestCase)
            expect("DEGENERATE") { testCaseParser.getTestCase().functionName }
        }

        it("gets the test case description") {
            val testCaseParser = StringTestCaseReader(validTestCase)
            expect("Test case description") { testCaseParser.getTestCase().description }
        }

    }

    it("fails with invalid test case") {
        assertFails { StringTestCaseReader("") }
    }

    it("fails with empty test case description") {
        assertFails { StringTestCaseReader(ResourceHelper.getTestCaseByName("emptyDescription")).getTestCase() }
    }

    it("fails with empty function name") {
        assertFails { StringTestCaseReader(ResourceHelper.getTestCaseByName("emptyFunctionName")).getTestCase() }
    }


})