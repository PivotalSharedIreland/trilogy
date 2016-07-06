package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.Fixtures
import io.pivotal.trilogy.mocks.AssertionExecutorStub
import io.pivotal.trilogy.mocks.ScriptExecuterSpy
import io.pivotal.trilogy.mocks.TestSubjectCallerStub
import io.pivotal.trilogy.testcase.TestCaseHooks
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testproject.FixtureLibrary
import org.amshove.kluent.`should contain`
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import kotlin.test.expect


class DatabaseTestCaseRunnerTests : Spek({
    // this sucks, is there a way to declare these as null ?
    var testSubjectCallerStub = TestSubjectCallerStub()
    var assertionExecutorStub = AssertionExecutorStub()
    var scriptExecutorSpy = ScriptExecuterSpy()
    var testCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecutorStub, scriptExecutorSpy)
    val testCaseHooks = TestCaseHooks(emptyList(), emptyList(), emptyList(), emptyList())

    beforeEach {
        testSubjectCallerStub = TestSubjectCallerStub()
        scriptExecutorSpy = ScriptExecuterSpy()
        assertionExecutorStub = AssertionExecutorStub()
        testCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecutorStub, scriptExecutorSpy)
    }
    context("with before all specified") {
        val firstSetupScript = "Per guest prepare eight pounds of remoulade with heated turkey for dessert."
        val secondSetupScript = "The collision course is an evil parasite."
        val fixtureLibrary = FixtureLibrary(mapOf(
                Pair("setup/set_client_balance", firstSetupScript),
                Pair("setup/update_client_messages", secondSetupScript)
        ))


        it("should run the setup script once") {
            val beforeAll = listOf("Set client balance")
            val hooks = TestCaseHooks(beforeAll, emptyList(), emptyList(), emptyList())
            val testCase = TrilogyTestCase("someProcedure", "someDescription", emptyList(), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)

            expect(1) { scriptExecutorSpy.executeCalls }
            scriptExecutorSpy.executeArgList `should contain` firstSetupScript
        }

        it("runs the before all steps in order") {
            val beforeAll = listOf("Set client balance", "UpdAte client Messages")
            val hooks = TestCaseHooks(beforeAll, emptyList(), emptyList(), emptyList())
            val testCase = TrilogyTestCase("someProcedure", "someDescription", emptyList(), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)

            expect(beforeAll.count()) { scriptExecutorSpy.executeCalls }
            scriptExecutorSpy.executeArgList[0] shouldEqual firstSetupScript
            scriptExecutorSpy.executeArgList[1] shouldEqual secondSetupScript
        }
    }
    context("when the test case has no tests") {
        it("should run test case successfully") {
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
