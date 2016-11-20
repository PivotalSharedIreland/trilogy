package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.Fixtures
import io.pivotal.trilogy.isEven
import io.pivotal.trilogy.mocks.AssertionExecuterMock
import io.pivotal.trilogy.mocks.ScriptExecuterMock
import io.pivotal.trilogy.mocks.TestSubjectCallerStub
import io.pivotal.trilogy.testcase.GenericTrilogyTest
import io.pivotal.trilogy.testcase.GenericTrilogyTestCase
import io.pivotal.trilogy.testcase.ProcedureTrilogyTestCase
import io.pivotal.trilogy.testcase.TestCaseHooks
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testproject.FixtureLibrary
import org.amshove.kluent.`should contain`
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class DatabaseTestCaseRunnerTests : Spek({
    // this sucks, is there a way to declare these as null ?
    var testSubjectCallerStub = TestSubjectCallerStub()
    var scriptExecuterMock = ScriptExecuterMock()
    var assertionExecuterMock = AssertionExecuterMock(scriptExecuterMock)
    var testCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecuterMock, scriptExecuterMock)
    val testCaseHooks = TestCaseHooks()
    val firstSetupScript = "First setup script"
    val secondSetupScript = "Second setup script"
    val thirdSetupScript = "Third setup script"
    val firstTeardownScript = "First teardown script"
    val secondTeardownScript = "Second teardown script"
    val thirdTeardownScript = "Third teardown script"
    val fixtureLibrary = FixtureLibrary(mapOf(
            Pair("setup/set_client_balance", firstSetupScript),
            Pair("setup/update_client_messages", secondSetupScript),
            Pair("setup/update_client_balance", thirdSetupScript),
            Pair("teardown/clear_client_balance", firstTeardownScript),
            Pair("teardown/nowhere", secondTeardownScript),
            Pair("teardown/collision_course", thirdTeardownScript)
    ))



    beforeEach {
        testSubjectCallerStub = TestSubjectCallerStub()
        scriptExecuterMock = ScriptExecuterMock()
        assertionExecuterMock = AssertionExecuterMock(scriptExecuterMock)
        testCaseRunner = DatabaseTestCaseRunner(testSubjectCallerStub, assertionExecuterMock, scriptExecuterMock)
    }

    context("generic") {
        it("runs an empty test case") {
            val testCase = GenericTrilogyTestCase("foo", emptyList(), TestCaseHooks())
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect(0) { result.failed }
            expect(0) { result.passed }
        }

        it("evaluates the body of the test") {
            val testCase = GenericTrilogyTestCase("foo", listOf(GenericTrilogyTest("", "O, jolly desolation!", emptyList())), TestCaseHooks())
            testCaseRunner.run(testCase, fixtureLibrary)

            expect(1) { scriptExecuterMock.executeCalls }
            expect("O, jolly desolation!") { scriptExecuterMock.executeArgList.first() }
        }

        it("reports a passing test") {
            val testCase = GenericTrilogyTestCase("o", listOf(GenericTrilogyTest("", "", emptyList())), TestCaseHooks())
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect(1) { result.passed }
            expect(0) { result.failed }
        }

        it("reports a failing test on the test failure") {
            val testCase = GenericTrilogyTestCase("o", listOf(GenericTrilogyTest("", "", emptyList())), TestCaseHooks())
            scriptExecuterMock.shouldFailExecution = true
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect(0) { result.passed }
            expect(1) { result.failed }
        }

        it("reports a failing test on the assertion failure") {
            val testWithAssertions = GenericTrilogyTest("", "", listOf(TrilogyAssertion("", "")))
            val testCase = GenericTrilogyTestCase("o", listOf(testWithAssertions), TestCaseHooks())
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect(0) { result.passed }
            expect(1) { result.failed }
        }

        it("reports a passing test on successful assertion") {
            val testWithAssertions = GenericTrilogyTest("", "", listOf(TrilogyAssertion("", "")))
            val testCase = GenericTrilogyTestCase("o", listOf(testWithAssertions), TestCaseHooks())
            assertionExecuterMock.passAllExecutedAssertions = true
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect(1) { result.passed }
            expect(0) { result.failed }
        }
    }

    context("hooks") {
        context("with before all specified") {


            it("should run the setup script once") {
                val beforeAll = listOf("Set client balance")
                val hooks = TestCaseHooks(beforeAll = beforeAll)
                val testCase = ProcedureTrilogyTestCase("someProcedure", "someDescription", emptyList(), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)

                expect(1) { scriptExecuterMock.executeCalls }
                scriptExecuterMock.executeArgList `should contain` firstSetupScript
            }

            it("runs the before all steps in order") {
                val beforeAll = listOf("Set client balance", "UpdAte client Messages")
                val hooks = TestCaseHooks(beforeAll = beforeAll)
                val testCase = ProcedureTrilogyTestCase("someProcedure", "someDescription", emptyList(), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)

                expect(beforeAll.count()) { scriptExecuterMock.executeCalls }
                scriptExecuterMock.executeArgList[0] shouldEqual firstSetupScript
                scriptExecuterMock.executeArgList[1] shouldEqual secondSetupScript
            }
        }

        context("with before each row specified") {
            it("should run the before each row script once for each row") {
                val beforeEachRow = listOf("Set client balance")
                val hooks = TestCaseHooks(beforeEachRow = beforeEachRow)
                val testCase = ProcedureTrilogyTestCase("someProcedure", "someDescription", listOf(Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)
                expect(3) { scriptExecuterMock.executeCalls }
            }

            it("should run the before each row scripts in sequence") {
                val beforeEachRow = listOf("Set client balance", "upDate client messages")
                val hooks = TestCaseHooks(beforeEachRow = beforeEachRow)
                val testCase = ProcedureTrilogyTestCase("someProcedure", "someDescription", listOf(Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)
                expect(6) { scriptExecuterMock.executeCalls }
                scriptExecuterMock.executeArgList.forEachIndexed { index, script ->
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
                val testCase = ProcedureTrilogyTestCase("someProcedure", "someDescription", listOf(Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)
                expect(3) { scriptExecuterMock.executeCalls }
            }

            it("should run the scripts in sequence") {
                val afterEachRow = listOf("Clear Client BalaNce", "NOwhere")
                val hooks = TestCaseHooks(afterEachRow = afterEachRow)
                val testCase = ProcedureTrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)
                expect(6) { scriptExecuterMock.executeCalls }
                scriptExecuterMock.executeArgList.forEachIndexed { index, script ->
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
                val testCase = ProcedureTrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)
                expect(1) { scriptExecuterMock.executeCalls }
            }

            it("should run each script in order") {
                val afterEachTest = listOf("nowhere", "CLEAR client BALANCE")
                val hooks = TestCaseHooks(afterEachTest = afterEachTest)
                val testCase = ProcedureTrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)

                expect(2) { scriptExecuterMock.executeCalls }
                scriptExecuterMock.executeArgList.first() shouldEqual secondTeardownScript
                scriptExecuterMock.executeArgList.last() shouldEqual firstTeardownScript
            }
        }

        context("with after all") {
            it("should run after all") {
                val afterAll = listOf("nowhere")
                val hooks = TestCaseHooks(afterAll = afterAll)
                val testCase = ProcedureTrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)
                expect(1) { scriptExecuterMock.executeCalls }
            }

            it("should run each script in order") {
                val afterAll = listOf("nowhere", "CLEAR client BALANCE")
                val hooks = TestCaseHooks(afterAll = afterAll)
                val testCase = ProcedureTrilogyTestCase("foo", "bar", listOf(Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)

                expect(2) { scriptExecuterMock.executeCalls }
                scriptExecuterMock.executeArgList.first() shouldEqual secondTeardownScript
                scriptExecuterMock.executeArgList.last() shouldEqual firstTeardownScript
            }

        }

        context("with before each test") {
            it("should run the script once for each test") {
                val beforeEachTest = listOf("set client balance")
                val hooks = TestCaseHooks(beforeEachTest = beforeEachTest)
                val testCase = ProcedureTrilogyTestCase("boo", "far", listOf(Fixtures.testWithThreeRows, Fixtures.testWithThreeRows), hooks)

                testCaseRunner.run(testCase, fixtureLibrary)
                expect(2) { scriptExecuterMock.executeCalls }
            }


        }
    }

    context("procedural") {
        it("runs all the scripts in sync") {
            val hooks = TestCaseHooks(
                    beforeAll = listOf("set client balance"),
                    beforeEachTest = listOf("update client messages"),
                    beforeEachRow = listOf("update client balance"),
                    afterEachRow = listOf("clear client balance"),
                    afterEachTest = listOf("nowhere"),
                    afterAll = listOf("collision course")
            )
            val firstTestAssertionScript = "First test assertion"
            val secondTestAssertionScript = "Second test assertion"
            val firstTest = Fixtures.testWithThreeRowsAndAssertions(listOf(TrilogyAssertion("", firstTestAssertionScript)))
            val secondTest = Fixtures.testWithThreeRowsAndAssertions(listOf(TrilogyAssertion("", secondTestAssertionScript)))
            val testCase = ProcedureTrilogyTestCase("boo", "bar", listOf(firstTest, secondTest), hooks)

            val beforeAllScript = firstSetupScript
            val beforeEachTestScript = secondSetupScript
            val beforeEachRowScript = thirdSetupScript
            val afterEachRowScript = firstTeardownScript
            val afterEachTestScript = secondTeardownScript
            val afterAllScript = thirdTeardownScript

            testCaseRunner.run(testCase, fixtureLibrary)
            scriptExecuterMock.executeArgList shouldEqual listOf(
                    beforeAllScript,
                    beforeEachTestScript,

                    beforeEachRowScript,
                    firstTestAssertionScript,
                    afterEachRowScript,

                    beforeEachRowScript,
                    firstTestAssertionScript,
                    afterEachRowScript,

                    beforeEachRowScript,
                    firstTestAssertionScript,
                    afterEachRowScript,

                    afterEachTestScript,

                    beforeEachTestScript,

                    beforeEachRowScript,
                    secondTestAssertionScript,
                    afterEachRowScript,

                    beforeEachRowScript,
                    secondTestAssertionScript,
                    afterEachRowScript,

                    beforeEachRowScript,
                    secondTestAssertionScript,
                    afterEachRowScript,

                    afterEachTestScript,
                    afterAllScript
            )
        }

        context("when the test case has no tests") {
            it("should run test case successfully") {
                expect(true) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", emptyList(), testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
            }
        }

        context("when the assertions pass and the output is the expected output") {
            beforeEach {
                assertionExecuterMock.passAllExecutedAssertions = true
                testSubjectCallerStub.resultToReturn = mapOf("OUT" to "1")
            }

            context("given there is only a single test") {
                val singleTest = Fixtures.buildSingleTest()

                it("then the test case should pass") {
                    expect(true) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
                }

                it("then the number of successful tests should be reported on") {
                    expect(1) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).passed }
                }
            }

            context("when there are multiple tests to run") {
                val multipleTests = Fixtures.buildMultipleTests()

                it("then the number of successful tests should be reported on") {
                    expect(3) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", multipleTests, testCaseHooks), FixtureLibrary.emptyLibrary()).passed }
                }
            }
        }

        context("when the output is expected but an assertion fails") {
            beforeEach {
                assertionExecuterMock.passAllExecutedAssertions = false
                testSubjectCallerStub.resultToReturn = mapOf("OUT" to "1")
            }

            context("when a single test is run") {
                val singleTest = Fixtures.buildSingleTest()

                it("then the test case should fail") {
                    expect(false) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
                }

                it("then a single failure should be reported on") {
                    expect(1) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).failed }
                }
            }

            context("when multiple tests run") {
                val multipleTests = Fixtures.buildMultipleTests()

                it("then multiple failures should be reported on") {
                    expect(3) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", multipleTests, testCaseHooks), FixtureLibrary.emptyLibrary()).failed }
                }
            }
        }

        context("when the assertion passes but the output is not the expected output") {
            beforeEach {
                assertionExecuterMock.passAllExecutedAssertions = true
                testSubjectCallerStub.resultToReturn = mapOf("OUT" to "2")
            }

            it("then the test case should fail") {
                val singleTest = Fixtures.buildSingleTest()
                expect(false) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
            }
        }
    }
})
