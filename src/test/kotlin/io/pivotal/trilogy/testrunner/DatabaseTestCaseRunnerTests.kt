package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.Fixtures
import io.pivotal.trilogy.isEven
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
    val testCaseHooks = TestCaseHooks()
    val firstSetupScript = "Per guest prepare eight pounds of remoulade with heated turkey for dessert."
    val secondSetupScript = "The collision course is an evil parasite."
    val firstTeardownScript = "Nirvana of dogma will theosophically shape a closest body."
    val secondTeardownScript = "C'mon, arrr."
    val fixtureLibrary = FixtureLibrary(mapOf(
            Pair("setup/set_client_balance", firstSetupScript),
            Pair("setup/update_client_messages", secondSetupScript),
            Pair("teardown/clear_client_balance", firstTeardownScript),
            Pair("teardown/nowhere", secondTeardownScript)
    ))



    beforeEach {
        testSubjectCallerStub = TestSubjectCallerStub()
        scriptExecutorSpy = ScriptExecuterSpy()
        assertionExecutorStub = AssertionExecutorStub()
        testCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecutorStub, scriptExecutorSpy)
    }
    context("with before all specified") {


        it("should run the setup script once") {
            val beforeAll = listOf("Set client balance")
            val hooks = TestCaseHooks(beforeAll = beforeAll)
            val testCase = TrilogyTestCase("someProcedure", "someDescription", emptyList(), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)

            expect(1) { scriptExecutorSpy.executeCalls }
            scriptExecutorSpy.executeArgList `should contain` firstSetupScript
        }

        it("runs the before all steps in order") {
            val beforeAll = listOf("Set client balance", "UpdAte client Messages")
            val hooks = TestCaseHooks(beforeAll = beforeAll)
            val testCase = TrilogyTestCase("someProcedure", "someDescription", emptyList(), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)

            expect(beforeAll.count()) { scriptExecutorSpy.executeCalls }
            scriptExecutorSpy.executeArgList[0] shouldEqual firstSetupScript
            scriptExecutorSpy.executeArgList[1] shouldEqual secondSetupScript
        }
    }

    context("with before each row specified") {
        it("should run the before each row script once for each row") {
            val beforeEachRow = listOf("Set client balance")
            val hooks = TestCaseHooks(beforeEachRow = beforeEachRow)
            val testCase = TrilogyTestCase("someProcedure", "someDescription", listOf(Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)
            expect(3) { scriptExecutorSpy.executeCalls }
        }

        it("should run the before each row scripts in sequence") {
            val beforeEachRow = listOf("Set client balance", "upDate client messages")
            val hooks = TestCaseHooks(beforeEachRow = beforeEachRow)
            val testCase = TrilogyTestCase("someProcedure", "someDescription", listOf(Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)
            expect(6) { scriptExecutorSpy.executeCalls }
            scriptExecutorSpy.executeArgList.forEachIndexed { index, script ->
                if (index.isEven)
                    script shouldEqual firstSetupScript
                else
                    script shouldEqual secondSetupScript
            }
        }
    }

    context("with after each row scripts") {
        it("should run the script once for each row") {
            val afterEachRow = listOf("Clear client balance")
            val hooks = TestCaseHooks(afterEachRow = afterEachRow)
            val testCase = TrilogyTestCase("someProcedure", "someDescription", listOf(Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)
            expect(3) { scriptExecutorSpy.executeCalls }
        }

        it("should run the scripts in sequence") {
            val afterEachRow = listOf("Clear Client BalaNce", "NOwhere")
            val hooks = TestCaseHooks(afterEachRow = afterEachRow)
            val testCase = TrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)
            expect(6) { scriptExecutorSpy.executeCalls }
            scriptExecutorSpy.executeArgList.forEachIndexed { index, script ->
                if (index.isEven)
                    script shouldEqual firstTeardownScript
                else
                    script shouldEqual secondTeardownScript
            }
        }
    }
    context("with after each test") {
        it("should run after each test") {
            val afterEachTest = listOf("nowhere")
            val hooks = TestCaseHooks(afterEachTest = afterEachTest)
            val testCase = TrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)
            expect(1) { scriptExecutorSpy.executeCalls }
        }

        it("should run each script in order") {
            val afterEachTest = listOf("nowhere", "CLEAR client BALANCE")
            val hooks = TestCaseHooks(afterEachTest = afterEachTest)
            val testCase = TrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)

            expect(2) { scriptExecutorSpy.executeCalls }
            scriptExecutorSpy.executeArgList.first() shouldEqual secondTeardownScript
            scriptExecutorSpy.executeArgList.last() shouldEqual firstTeardownScript
        }
    }

    context("with after all") {
        it ("should run after all") {
            val afterAll = listOf("nowhere")
            val hooks = TestCaseHooks(afterAll = afterAll)
            val testCase = TrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)
            expect(1) { scriptExecutorSpy.executeCalls }
        }

        it("should run each script in order") {
            val afterAll = listOf("nowhere", "CLEAR client BALANCE")
            val hooks = TestCaseHooks(afterAll = afterAll)
            val testCase = TrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)

            expect(2) { scriptExecutorSpy.executeCalls }
            scriptExecutorSpy.executeArgList.first() shouldEqual secondTeardownScript
            scriptExecutorSpy.executeArgList.last() shouldEqual firstTeardownScript
        }

    }

    context("with before each test") {
        it("should run the script once for each test") {
            val beforeEachTest = listOf("set client balance")
            val hooks = TestCaseHooks(beforeEachTest = beforeEachTest)
            val testCase = TrilogyTestCase("boo", "far", listOf(Fixtures.testWithThreeRows, Fixtures.testWithThreeRows), hooks)

            testCaseRunner.run(testCase, fixtureLibrary)
            expect(2) { scriptExecutorSpy.executeCalls }
        }


    }
    // everything in sync 1

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
