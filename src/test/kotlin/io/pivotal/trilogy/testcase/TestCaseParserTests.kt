package io.pivotal.trilogy.testcase

import io.pivotal.trilogy.ResourceHelper
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails

class TestCaseParserTests : Spek ({
    val validTestCase = ResourceHelper.getTestCaseByName("degenerate")

    describe("foo") {
        xit("succeeds when initializing with a valid test case") {
            TestCaseParser(validTestCase)
        }

        it("fails when initializing an invalid test case") {
            assertFails { TestCaseParser("") }
        }
    }
})