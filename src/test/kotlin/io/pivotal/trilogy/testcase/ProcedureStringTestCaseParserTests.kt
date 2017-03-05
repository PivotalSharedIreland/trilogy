package io.pivotal.trilogy.testcase

import io.pivotal.trilogy.parsing.ProcedureStringTestCaseParser
import io.pivotal.trilogy.test_helpers.ResourceHelper
import io.pivotal.trilogy.test_helpers.shouldNotThrow
import org.amshove.kluent.AnyException
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect

class ProcedureStringTestCaseParserTests : Spek({

    describe("degenerate") {
        val validTestCase = ResourceHelper.getTestCaseByName("degenerate")

        it("succeeds with a valid test case") {
            ProcedureStringTestCaseParser(validTestCase)
        }

        it("gets a valid test case name") {
            val testCaseParser = ProcedureStringTestCaseParser(validTestCase)
            expect("DEGENERATE") { (testCaseParser.getTestCase() as ProcedureTrilogyTestCase).procedureName }
        }

        it("gets the test case description") {
            val testCaseParser = ProcedureStringTestCaseParser(validTestCase)
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
            val test = ValidProcedureTrilogyTest("Test description", arguments, emptyList())

            expect(test) { ProcedureStringTestCaseParser(validTestCase).getTestCase().tests.first() }
        }

        it("should parse as a procedure test case") {
            expect(true) { ProcedureStringTestCaseParser(validTestCase).getTestCase() is ProcedureTrilogyTestCase }
        }
    }

    describe("broken procedural tests") {
        val brokenProceduralTests = ResourceHelper.getTestCaseByName("broken_procedural_tests")

        it("succeeds with parsing the whole test case") {
            { ProcedureStringTestCaseParser(brokenProceduralTests).getTestCase() } shouldNotThrow AnyException
        }

        it("has some malformed tests") {
            val testCase = ProcedureStringTestCaseParser(brokenProceduralTests).getTestCase()
            expect(2) { testCase.malformedTests.count() }
        }

        it("sets the failure message") {
            val testCase = ProcedureStringTestCaseParser(brokenProceduralTests).getTestCase()
            val malformedTest = testCase.malformedTests[0]

            expect("DATA section is missing from a procedural test") { malformedTest.errorMessage }
        }

    }

    describe("malformed data in procedural tests") {
        val malformedDataProceduralTests = ResourceHelper.getTestCaseByName("malformed_data_section")

        it("succeeds when parsing the whole test case") {
            { ProcedureStringTestCaseParser(malformedDataProceduralTests).getTestCase() } shouldNotThrow AnyException
        }

        it("has a malformed test") {
            val testCase = ProcedureStringTestCaseParser(malformedDataProceduralTests).getTestCase()
            expect(1) { testCase.malformedTests.count() }
        }

        it("sets the failure message") {
            val testCase = ProcedureStringTestCaseParser(malformedDataProceduralTests).getTestCase()
            val malformedTest = testCase.malformedTests[0]

            expect("Expected the DATA section to contain a test table") { malformedTest.errorMessage }
        }
    }

    describe("multiple tests") {
        val validTestCase = ResourceHelper.getTestCaseByName("multiple/shouldPass")
        val testCase = ProcedureStringTestCaseParser(validTestCase).getTestCase()

        it("should return two tests") {
            expect(2) { testCase.tests.count() }
        }

        it("should return empty fixture hook lists") {
            expect(true) { testCase.hooks.beforeAll.isEmpty() }
            expect(true) { testCase.hooks.beforeEachRow.isEmpty() }
            expect(true) { testCase.hooks.beforeEachTest.isEmpty() }
            expect(true) { testCase.hooks.afterAll.isEmpty() }
            expect(true) { testCase.hooks.afterEachRow.isEmpty() }
            expect(true) { testCase.hooks.afterEachTest.isEmpty() }
        }
    }

    describe("fixture hooks") {
        val testCase = ResourceHelper.getTestCaseByName("projectBased/setupTeardown")
        val testCaseHooks = ProcedureStringTestCaseParser(testCase).getTestCase().hooks

        it("should extract before all hook names") {
            val beforeAllHooks = testCaseHooks.beforeAll
            expect(3) { beforeAllHooks.count() }
            expect("Setup client") { beforeAllHooks.first() }
            expect("Ships reproduce with xray vision") { beforeAllHooks[1] }
            expect("With melons drink maple syrup") { beforeAllHooks.last() }
        }

        it("should extract before each test hook names") {
            val beforeEachTestHooks = testCaseHooks.beforeEachTest
            expect(3) { beforeEachTestHooks.count() }
            expect("Set client balance") { beforeEachTestHooks.first() }
            expect("Grace life and passion") { beforeEachTestHooks[1] }
            expect("With tunas drink tea") { beforeEachTestHooks.last() }
        }

        it("should extract before each row hook names") {
            val beforeEachRowHooks = testCaseHooks.beforeEachRow
            expect(2) { beforeEachRowHooks.count() }
            expect("Contencio flavum vita est") { beforeEachRowHooks.first() }
            expect("Everyone just loves the fierceness of chicken cheesecake flavord with cumin.") { beforeEachRowHooks[1] }
        }

        it("should extract after all hook names") {
            val afterAllHooks = testCaseHooks.afterAll
            expect(2) { afterAllHooks.count() }
            expect("Remove clients") { afterAllHooks.first() }
            expect("Fraticinidas ire") { afterAllHooks.last() }
        }

        it("should extract after each test hook names") {
            val afterEachTestHooks = testCaseHooks.afterEachTest
            expect(2) { afterEachTestHooks.count() }
            expect("Remove transactions") { afterEachTestHooks.first() }
            expect("Be mysterious") { afterEachTestHooks.last() }
        }

        it("should extract after each row hook names") {
            val afterEachRowHooks = testCaseHooks.afterEachRow
            expect(1) { afterEachRowHooks.count() }
            expect("Always solitary yearn the spiritual saint.") { afterEachRowHooks.first() }
        }
    }

    describe("Empty fixture hook sections") {
        val validTestCase = ResourceHelper.getTestCaseByName("projectBased/blankSetupTeardown")
        val testCaseHooks = ProcedureStringTestCaseParser(validTestCase).getTestCase().hooks

        it("should return empty fixture hook lists") {
            expect(true) { testCaseHooks.beforeAll.isEmpty() }
            expect(true) { testCaseHooks.beforeEachTest.isEmpty() }
            expect(true) { testCaseHooks.beforeEachRow.isEmpty() }
            expect(true) { testCaseHooks.afterAll.isEmpty() }
            expect(true) { testCaseHooks.afterEachTest.isEmpty() }
            expect(true) { testCaseHooks.afterEachRow.isEmpty() }
        }

    }

    it("fails with invalid test case") {
        assertFails { ProcedureStringTestCaseParser("") }
    }

    it("fails with empty function name") {
        assertFails { ProcedureStringTestCaseParser(ResourceHelper.getTestCaseByName("emptyFunctionName")).getTestCase() }
    }


})
