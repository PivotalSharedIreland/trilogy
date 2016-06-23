package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.mocks.AssertionExecutorStub
import io.pivotal.trilogy.mocks.TestSubjectCallerStub
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTest
import io.pivotal.trilogy.testcase.TrilogyTestCase
import org.jetbrains.spek.api.Spek
import kotlin.test.expect


class DatabaseTestCaseRunnerTests : Spek({
    // this sucks, is there a way to declare these as null ?
    var testSubjectCallerStub: TestSubjectCallerStub = TestSubjectCallerStub()
    var assertionExecutorStub: AssertionExecutorStub = AssertionExecutorStub()
    var testCaseRunner: DatabaseTestCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecutorStub)

    beforeEach {
        testSubjectCallerStub = TestSubjectCallerStub()
        assertionExecutorStub = AssertionExecutorStub()
        testCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecutorStub)
    }

    context("when the test case has no tests") {
        it("then the test case should pass") {
            expect(true) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", emptyList())).didPass }
        }
    }

    context("when the assertions pass and the output is the expected output") {
        beforeEach {
            assertionExecutorStub.passAllExecutedAssertions = true
            testSubjectCallerStub.resultToReturn = mapOf("OUT" to "1")
        }

        context("given there is only a single test") {
            val singleTest = buildSingleTest()

            it("then the test case should pass") {
                expect(true) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest)).didPass }
            }

            it("then the number of successful tests should be reported on") {
                expect(1) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest)).passed }
            }
        }

        context("when there are multiple tests to run") {
            val multipleTests = buildMultipleTests()

            it("then the number of successful tests should be reported on") {
                expect(3) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", multipleTests)).passed }
            }
        }
    }

    context("when the output is expected but an assertion fails") {
        beforeEach {
            assertionExecutorStub.passAllExecutedAssertions = false
            testSubjectCallerStub.resultToReturn = mapOf("OUT" to "1")
        }

        context("when a single test is run") {
            val singleTest = buildSingleTest()

            it("then the test case should fail") {
                expect(false) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest)).didPass }
            }

            it("then a single failure should be reported on") {
                expect(1) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest)).failed }
            }
        }

        context("when multiple tests run") {
            val multipleTests = buildMultipleTests()

            it("then multiple failures should be reported on") {
                expect(3) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", multipleTests)).failed }
            }
        }
    }

    context("when the assertion passes but the output is not the expected output") {
        beforeEach {
            assertionExecutorStub.passAllExecutedAssertions = true
            testSubjectCallerStub.resultToReturn = mapOf("OUT" to "2")
        }

        it("then the test case should fail") {
            val singleTest = buildSingleTest()
            expect(false) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest)).didPass }
        }
    }
})

val argumentTable = TestArgumentTable(listOf("IN", "OUT$"), listOf(listOf("1", "1")))
val assertions = listOf(TrilogyAssertion("some description", "some SQL"))

fun buildSingleTest(): List<TrilogyTest> = listOf(TrilogyTest("I am a test", argumentTable, assertions))

fun buildMultipleTests(): List<TrilogyTest> = listOf(
        TrilogyTest("I am a test", argumentTable, assertions),
        TrilogyTest("I am also a test", argumentTable, assertions),
        TrilogyTest("Me three", argumentTable, assertions)
)
