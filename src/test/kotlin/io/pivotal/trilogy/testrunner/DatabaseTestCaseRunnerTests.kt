package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.Fixtures
import io.pivotal.trilogy.mocks.AssertionExecutorStub
import io.pivotal.trilogy.mocks.TestSubjectCallerStub
import io.pivotal.trilogy.testcase.TestCaseHooks
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testproject.FixtureLibrary
import org.jetbrains.spek.api.Spek
import kotlin.test.expect


class DatabaseTestCaseRunnerTests : Spek({
    // this sucks, is there a way to declare these as null ?
    var testSubjectCallerStub: TestSubjectCallerStub = TestSubjectCallerStub()
    var assertionExecutorStub: AssertionExecutorStub = AssertionExecutorStub()
    var testCaseRunner: DatabaseTestCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecutorStub)
    val testCaseHooks = TestCaseHooks(emptyList(), emptyList(), emptyList(), emptyList())

    beforeEach {
        testSubjectCallerStub = TestSubjectCallerStub()
        assertionExecutorStub = AssertionExecutorStub()
        testCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecutorStub)
    }

    context("when the test case has no tests") {
        it("then the test case should pass") {
            expect(true) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", emptyList(), testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
        }
    }

    context("when the assertions pass and the output is the expected output") {
        beforeEach {
            assertionExecutorStub.passAllExecutedAssertions = true
            testSubjectCallerStub.resultToReturn = mapOf("OUT" to "1")
        }

        context("given there is only a single test") {
            val singleTest = Fixtures.buildSingleTest()

            it("then the test case should pass") {
                expect(true) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
            }

            it("then the number of successful tests should be reported on") {
                expect(1) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).passed }
            }
        }

        context("when there are multiple tests to run") {
            val multipleTests = Fixtures.buildMultipleTests()

            it("then the number of successful tests should be reported on") {
                expect(3) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", multipleTests, testCaseHooks), FixtureLibrary.emptyLibrary()).passed }
            }
        }
    }

    context("when the output is expected but an assertion fails") {
        beforeEach {
            assertionExecutorStub.passAllExecutedAssertions = false
            testSubjectCallerStub.resultToReturn = mapOf("OUT" to "1")
        }

        context("when a single test is run") {
            val singleTest = Fixtures.buildSingleTest()

            it("then the test case should fail") {
                expect(false) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
            }

            it("then a single failure should be reported on") {
                expect(1) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).failed }
            }
        }

        context("when multiple tests run") {
            val multipleTests = Fixtures.buildMultipleTests()

            it("then multiple failures should be reported on") {
                expect(3) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", multipleTests, testCaseHooks), FixtureLibrary.emptyLibrary()).failed }
            }
        }
    }

    context("when the assertion passes but the output is not the expected output") {
        beforeEach {
            assertionExecutorStub.passAllExecutedAssertions = true
            testSubjectCallerStub.resultToReturn = mapOf("OUT" to "2")
        }

        it("then the test case should fail") {
            val singleTest = Fixtures.buildSingleTest()
            expect(false) { testCaseRunner.run(TrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
        }
    }
})
