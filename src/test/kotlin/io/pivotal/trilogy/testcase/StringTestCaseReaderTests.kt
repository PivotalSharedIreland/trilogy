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

        it("parses the test") {
            val header = listOf("PARAM1", "PARAM2", "=ERROR=")
            val values = listOf(
                    listOf("FOO", "12", ""),
                    listOf("__NULL__", "0", ""),
                    listOf("BAR", "-18", ""),
                    listOf("", "12", "")
            )
            val arguments = TestArgumentTable(header, values)
            val test = TrilogyTest("Test description", arguments, emptyList())

            expect(test) { StringTestCaseReader(validTestCase).getTestCase().tests.first() }
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