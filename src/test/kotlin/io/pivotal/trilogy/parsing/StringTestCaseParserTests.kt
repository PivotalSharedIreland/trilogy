package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.ResourceHelper
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyTest
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect

class StringTestCaseParserTests : Spek ({

    describe("degenerate") {
        val validTestCase = ResourceHelper.getTestCaseByName("degenerate")

        it("succeeds with a valid test case") {
            StringTestCaseParser(validTestCase)
        }

        it("gets a valid test case name") {
            val testCaseParser = StringTestCaseParser(validTestCase)
            expect("DEGENERATE") { testCaseParser.getTestCase().procedureName }
        }

        it("gets the test case description") {
            val testCaseParser = StringTestCaseParser(validTestCase)
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

            expect(test) { StringTestCaseParser(validTestCase).getTestCase().tests.first() }
        }

    }

    describe("multiple tests") {
        val validTestCase = ResourceHelper.getTestCaseByName("multiple/shouldPass")
        val testCase = StringTestCaseParser(validTestCase).getTestCase()

        it("should return two tests") {
            expect(2) { testCase.tests.count() }
        }

        it("should return empty fixture hook lists") {
            expect(true) { testCase.hooks.beforeAll.isEmpty() }
            expect(true) { testCase.hooks.beforeEach.isEmpty() }
            expect(true) { testCase.hooks.afterAll.isEmpty() }
            expect(true) { testCase.hooks.afterEach.isEmpty() }
        }
    }

    describe("fixture hooks") {
        val testCase = ResourceHelper.getTestCaseByName("projectBased/setupTeardown")
        val testCaseHooks = StringTestCaseParser(testCase).getTestCase().hooks

        it("should extract before all hook names") {
            val beforeAllHooks = testCaseHooks.beforeAll
            expect(3) { beforeAllHooks.count() }
            expect("Setup client") { beforeAllHooks.first() }
            expect("Ships reproduce with xray vision") { beforeAllHooks[1] }
            expect("With melons drink maple syrup") { beforeAllHooks.last() }
        }

        it("should extract before each hook names") {
            val beforeEachHooks = testCaseHooks.beforeEach
            expect(3) { beforeEachHooks.count() }
            expect("Set client balance") { beforeEachHooks.first() }
            expect("Grace life and passion") { beforeEachHooks[1] }
            expect("With tunas drink tea") { beforeEachHooks.last() }
        }

        it("should extract after all hook names") {
            val afterAllHooks = testCaseHooks.afterAll
            expect(2) { afterAllHooks.count() }
            expect("Remove clients") { afterAllHooks.first() }
            expect("Fraticinidas ire") { afterAllHooks.last() }
        }

        it("should extract after each hook names") {
            val afterEachHooks = testCaseHooks.afterEach
            expect(2) { afterEachHooks.count() }
            expect("Remove transactions") { afterEachHooks.first() }
            expect("Be mysterious") { afterEachHooks.last() }
        }
    }

    describe("Empty fixture hook sections") {
        val validTestCase = ResourceHelper.getTestCaseByName("projectBased/blankSetupTeardown")
        val testCaseHooks = StringTestCaseParser(validTestCase).getTestCase().hooks

        it("should return empty fixture hook lists") {
            expect(true) { testCaseHooks.beforeAll.isEmpty() }
            expect(true) { testCaseHooks.beforeEach.isEmpty() }
            expect(true) { testCaseHooks.afterAll.isEmpty() }
            expect(true) { testCaseHooks.afterEach.isEmpty() }
        }

    }

    it("fails with invalid test case") {
        assertFails { StringTestCaseParser("") }
    }

    it("fails with empty test case description") {
        assertFails { StringTestCaseParser(ResourceHelper.getTestCaseByName("emptyDescription")).getTestCase() }
    }

    it("fails with empty function name") {
        assertFails { StringTestCaseParser(ResourceHelper.getTestCaseByName("emptyFunctionName")).getTestCase() }
    }


})
