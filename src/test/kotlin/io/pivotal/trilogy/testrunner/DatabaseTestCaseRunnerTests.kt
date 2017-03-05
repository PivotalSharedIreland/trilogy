package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.mocks.AssertionExecuterMock
import io.pivotal.trilogy.mocks.ScriptExecuterMock
import io.pivotal.trilogy.mocks.TestSubjectCallerStub
import io.pivotal.trilogy.test_helpers.Fixtures
import io.pivotal.trilogy.test_helpers.isEven
import io.pivotal.trilogy.test_helpers.shouldContain
import io.pivotal.trilogy.test_helpers.shouldThrow
import io.pivotal.trilogy.testcase.GenericTrilogyTest
import io.pivotal.trilogy.testcase.GenericTrilogyTestCase
import io.pivotal.trilogy.testcase.MalformedTrilogyTest
import io.pivotal.trilogy.testcase.ProcedureTrilogyTestCase
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TestCaseHooks
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.ValidProcedureTrilogyTest
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
    val hooksWithBeforeAll = TestCaseHooks(beforeAll = listOf("set client balance"))
    val hooksWithBeforeAllAndBeforeEachTest = TestCaseHooks(beforeAll = listOf("set client balance"),
            beforeEachTest = listOf("Update client messages"))
    val hooksWithBeforeEveryPossibleStep = TestCaseHooks(beforeAll = listOf("set client balance"),
            beforeEachTest = listOf("Update client messages"), beforeEachRow = listOf("Update client balance"))
    val hooksWithAfterEachRow = TestCaseHooks(afterEachRow = listOf("Clear client balance"))
    val hooksWithAfterEachTest = TestCaseHooks(afterEachTest = listOf("Nowhere"))
    val hooksWithAfterAll = TestCaseHooks(afterAll = listOf("Collision course"))

    val firstSetupScript = "First setup script"
    val secondSetupScript = "Second setup script"
    val thirdSetupScript = "Third setup script"
    val firstTeardownScript = "First teardown script"
    val secondTeardownScript = "Second teardown script"
    val thirdTeardownScript = "Third teardown script"
    val fixtureLibrary = FixtureLibrary(mapOf(
            "setup/set_client_balance" to firstSetupScript,
            "setup/update_client_messages" to secondSetupScript,
            "setup/update_client_balance" to thirdSetupScript,
            "teardown/clear_client_balance" to firstTeardownScript,
            "teardown/nowhere" to secondTeardownScript,
            "teardown/collision_course" to thirdTeardownScript
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

        it("includes the test case name in the result") {
            val testCase = GenericTrilogyTestCase("Flying teapot", emptyList(), TestCaseHooks())
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect("Flying teapot") { result.testCaseName }
        }

        it("includes test names in the result") {
            val tests = listOf(GenericTrilogyTest("Mashing the pork butts", "test body", emptyList()))
            val testCase = GenericTrilogyTestCase("", tests, TestCaseHooks())
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect("Mashing the pork butts") { result.tests.first().testName }
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

        it("includes the error message int the test report") {
            val testCase = GenericTrilogyTestCase("Why does the ship yell?..", listOf(GenericTrilogyTest("Caniss ire!", "oops!", emptyList())), TestCaseHooks())
            scriptExecuterMock.shouldFailExecution = true
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect("SQL Script exception") { result.failedTests.first().errorMessage }
        }

        it("reports a failing test on the assertion failure") {
            val testWithAssertions = GenericTrilogyTest("", "", listOf(TrilogyAssertion("", "")))
            assertionExecuterMock.assertionExecutionErrorMessage = "an assertion failure"
            val testCase = GenericTrilogyTestCase("o", listOf(testWithAssertions), TestCaseHooks())
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect(0) { result.passed }
            expect(1) { result.failed }
        }

        it("reports a passing test on successful assertion") {
            val testWithAssertions = GenericTrilogyTest("", "", listOf(TrilogyAssertion("", "")))
            val testCase = GenericTrilogyTestCase("o", listOf(testWithAssertions), TestCaseHooks())
            assertionExecuterMock.assertionExecutionErrorMessage = null
            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect(1) { result.passed }
            expect(0) { result.failed }
        }

        it("includes the malformed tests in the test case report") {
            val malformedTest = MalformedTrilogyTest("Cantare etiam", "Combine avocado, nachos and pork butt.")
            val testCase = GenericTrilogyTestCase("Jive", emptyList(), testCaseHooks, listOf(malformedTest))

            val result = testCaseRunner.run(testCase, fixtureLibrary)

            expect(0) { result.passed }
            expect(1) { result.failed }
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

        context("error handling") {
            val tests = listOf(ValidProcedureTrilogyTest("some test", TestArgumentTable(emptyList(), emptyList()), emptyList()))

            it("fails when a non-existing 'before all' fixture is specified") {
                val missingFixtureName = "I influence this beauty, it's called neutral vision."
                val hooks = TestCaseHooks(beforeAll = listOf(missingFixtureName))
                val testCase = ProcedureTrilogyTestCase("some_procedure", "some description", tests, hooks)

                val result = testCaseRunner.run(testCase, fixtureLibrary)

                expect(0) { scriptExecuterMock.executeCalls }
                expect("Unable to find fixture '$missingFixtureName'") { result.errorMessage }
                expect(0) { result.passed }
            }

            it("joins multiple fixture errors with newlines") {
                val fixture1 = "Jolly rogers scream with hunger at the golden tubbataha reef!"
                val fixture2 = "http://imgs.xkcd.com/comics/gnome_ann_2x.png"

                val hooks = TestCaseHooks(beforeAll = listOf(fixture1, fixture2))
                val testCase = ProcedureTrilogyTestCase("SOME_PROCEDURE", "Some description", tests, hooks)

                val result = testCaseRunner.run(testCase, fixtureLibrary)

                expect(0) { scriptExecuterMock.executeCalls }
                expect("Unable to find fixture '$fixture1'\nUnable to find fixture '$fixture2'") { result.errorMessage }
                expect(0) { result.passed }
            }

            it("fails when a non-existing 'before each test' fixture is specified") {
                val missingFixtureName = "When the individual of politics believes the stigmas of the scholar, the sainthood will know self."
                val hooks = TestCaseHooks(beforeEachTest = listOf(missingFixtureName))
                val testCase = ProcedureTrilogyTestCase("some_procedure", "some description", tests, hooks)

                val result = testCaseRunner.run(testCase, fixtureLibrary)

                expect(0) { scriptExecuterMock.executeCalls }
                expect(0) { result.passed }
                expect("Unable to find fixture '$missingFixtureName'") { result.errorMessage }
            }

            it("fails when a non-existing 'before each row' fixture is specified") {
                val missingFixtureName = "Try smashing blood oranges ricotta rinseed with champaign."
                val hooks = TestCaseHooks(beforeEachRow = listOf(missingFixtureName))
                val testCase = ProcedureTrilogyTestCase("some_procedure", "some description", tests, hooks)

                val result = testCaseRunner.run(testCase, fixtureLibrary)

                expect(0) { scriptExecuterMock.executeCalls }
                expect(0) { result.passed }
                expect("Unable to find fixture '$missingFixtureName'") { result.errorMessage }
            }

            it("fails when a non-existing 'after all' fixture is specified") {
                val missingFixtureName = "Luna, danista, et fortis."
                val hooks = TestCaseHooks(afterAll = listOf(missingFixtureName))
                val testCase = ProcedureTrilogyTestCase("some_procedure", "some description", tests, hooks)

                val result = testCaseRunner.run(testCase, fixtureLibrary)

                expect(0) { scriptExecuterMock.executeCalls }
                expect(0) { result.passed }
                expect("Unable to find fixture '$missingFixtureName'") { result.errorMessage }
            }

            it("fails when a non-existing 'after each test' fixture is specified") {
                val missingFixtureName = "The parasite yells mineral like an extraterrestrial mermaid."
                val hooks = TestCaseHooks(afterEachTest = listOf(missingFixtureName))
                val testCase = ProcedureTrilogyTestCase("some_procedure", "some description", tests, hooks)

                val result = testCaseRunner.run(testCase, fixtureLibrary)

                expect(0) { scriptExecuterMock.executeCalls }
                expect(0) { result.passed }
                expect("Unable to find fixture '$missingFixtureName'") { result.errorMessage }
            }

            it("fails when a non-existing 'after each row' fixture is specified") {
                val missingFixtureName = "Courage is an evil wind."
                val hooks = TestCaseHooks(afterEachRow = listOf(missingFixtureName))
                val testCase = ProcedureTrilogyTestCase("some_procedure", "some description", tests, hooks)

                val result = testCaseRunner.run(testCase, fixtureLibrary)

                expect(0) { scriptExecuterMock.executeCalls }
                expect(0) { result.passed }
                expect("Unable to find fixture '$missingFixtureName'") { result.errorMessage }
            }

            it("combines all the missing fixture error messages") {
                val missingFixtures = listOf(
                        "Fly tightly like a clear creature.",
                        "Sunt ventuses aperto gratis, raptus specieses.",
                        "Champaign soup is just not the same without black pepper and fresh salty turkey.",
                        "Wow, beauty!",
                        "Be inner.",
                        "Ahoy, sunny treasure!"
                )
                val hooks = TestCaseHooks(beforeAll = listOf(missingFixtures[0]), beforeEachTest = listOf(missingFixtures[1]),
                        beforeEachRow = listOf(missingFixtures[2]), afterEachRow = listOf(missingFixtures[3]),
                        afterEachTest = listOf(missingFixtures[4]), afterAll = listOf(missingFixtures[5]))
                val testCase = ProcedureTrilogyTestCase("Some_procedure", "Some description", tests, hooks)

                val result = testCaseRunner.run(testCase, fixtureLibrary)
                expect(0) { scriptExecuterMock.executeCalls }
                expect(0) { result.passed }
                missingFixtures.forEach {
                    result.errorMessage!! shouldContain "Unable to find fixture '$it'"
                }
            }

            context("fixture load failure") {
                val table = TestArgumentTable(listOf("FOO"), listOf(listOf("Bar")))
                val proceduralTest = ValidProcedureTrilogyTest("Gummy stuff", table, emptyList())
                val genericTest = GenericTrilogyTest("Fixture error test", "", emptyList())

                it("throws an exception when a 'before all' fixture load fails") {
                    scriptExecuterMock.shouldFailExecution = true
                    val trilogyTestCase = GenericTrilogyTestCase("Fixture error", listOf(genericTest), hooksWithBeforeAll);
                    { testCaseRunner.run(trilogyTestCase, fixtureLibrary) }.shouldThrow(FixtureLoadException::class)
                }

                it("describes the setup fixture failure in an unrecoverable error") {
                    scriptExecuterMock.shouldFailExecution = true
                    val trilogyTestCase = GenericTrilogyTestCase("Fixture error", listOf(genericTest), hooksWithBeforeAll)
                    val errorMessage = try {
                        testCaseRunner.run(trilogyTestCase, fixtureLibrary)
                        null
                    } catch (e: UnrecoverableException) {
                        e.localizedMessage
                    }
                    expect("Unable to load the 'set client balance' setup fixture\n    SQL Script exception") { errorMessage }
                }

                it("throws and exception when a 'before each test' fixture load fails") {
                    scriptExecuterMock.shouldFailAfter(safeExecutions = 1)
                    val trilogyTestCase = GenericTrilogyTestCase("Before each test failure", listOf(genericTest), hooksWithBeforeAllAndBeforeEachTest);
                    { testCaseRunner.run(trilogyTestCase, fixtureLibrary) }.shouldThrow(FixtureLoadException::class)
                }

                it("throws and exception when a 'before each row' fixture load fails") {
                    scriptExecuterMock.shouldFailAfter(safeExecutions = 2)
                    val trilogyTestCase = ProcedureTrilogyTestCase("DUMMY", "I failz", listOf(proceduralTest), hooksWithBeforeEveryPossibleStep);
                    { testCaseRunner.run(trilogyTestCase, fixtureLibrary) }.shouldThrow(FixtureLoadException::class)
                }

                it("throws and exception when an 'after each row' fixture load fails") {
                    scriptExecuterMock.shouldFailExecution = true
                    val trilogyTestCase = ProcedureTrilogyTestCase("MUDDY", "Lol!", listOf(proceduralTest), hooksWithAfterEachRow);
                    { testCaseRunner.run(trilogyTestCase, fixtureLibrary) }.shouldThrow(FixtureLoadException::class)
                }

                it("describes the teardown fixture failure in an unrecoverable error") {
                    scriptExecuterMock.shouldFailExecution = true
                    val trilogyTestCase = ProcedureTrilogyTestCase("QUOUOQ", "Sqwiwel!", listOf(proceduralTest), hooksWithAfterEachRow)
                    val errorMessage = try {
                        testCaseRunner.run(trilogyTestCase, fixtureLibrary)
                        null
                    } catch (e: UnrecoverableException) {
                        e.localizedMessage
                    }

                    expect("Unable to load the 'Clear client balance' teardown fixture\n    SQL Script exception") { errorMessage }
                }

                it("throws an exception when 'after each test' fixture load fails") {
                    scriptExecuterMock.shouldFailAfter(safeExecutions = 1)
                    val trilogyTestCase = GenericTrilogyTestCase("After Each test failure", listOf(genericTest), hooksWithAfterEachTest);
                    { testCaseRunner.run(trilogyTestCase, fixtureLibrary) }.shouldThrow(FixtureLoadException::class)
                }

                it("throws an exception when an 'after all' fixture load fails") {
                    scriptExecuterMock.shouldFailAfter(safeExecutions = 1)
                    val trilogyTestCase = GenericTrilogyTestCase("After all test failure", listOf(genericTest), hooksWithAfterAll);
                    { testCaseRunner.run(trilogyTestCase, fixtureLibrary) }.shouldThrow(FixtureLoadException::class)

                }

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

        it("includes the test case name in the result") {
            expect("Jolly Roger!") { testCaseRunner.run(ProcedureTrilogyTestCase("procName", "Jolly Roger!", emptyList(), testCaseHooks), FixtureLibrary.emptyLibrary()).testCaseName }
        }

        context("when the test case has no tests") {
            it("should run test case successfully") {
                expect(true) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", emptyList(), testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
            }
        }

        context("when the assertions pass and the output is the expected output") {
            beforeEach {
                assertionExecuterMock.assertionExecutionErrorMessage = null
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
                assertionExecuterMock.assertionExecutionErrorMessage = "some error message"
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
                assertionExecuterMock.assertionExecutionErrorMessage = null
                testSubjectCallerStub.resultToReturn = mapOf("OUT" to "2")
            }

            it("then the test case should fail") {
                val singleTest = Fixtures.buildSingleTest()
                expect(false) { testCaseRunner.run(ProcedureTrilogyTestCase("someProcedure", "someDescription", singleTest, testCaseHooks), FixtureLibrary.emptyLibrary()).didPass }
            }
        }

        context("when a specific error is expected") {
            val argumentTable = TestArgumentTable(listOf("=ERROR="), listOf(listOf("ERROR")))

            it("should report an error when no error is thrown") {
                val singleTest = ValidProcedureTrilogyTest("foo", argumentTable, emptyList())
                testSubjectCallerStub.resultToReturn = emptyMap()
                val expectedError = "Row 1 of 1: Expected an error with text 'ERROR' to occur, but no errors were triggered"
                val testCase = ProcedureTrilogyTestCase("someProcedure", "someDescription", listOf(singleTest), testCaseHooks)
                expect(expectedError) { testCaseRunner.run(testCase, FixtureLibrary.emptyLibrary()).tests.first().errorMessage }
            }
        }

        context("when any error is expected") {
            val argumentTable = TestArgumentTable(listOf("=ERROR="), listOf(listOf("ANY")))

            it("should report when no error is thrown") {
                val singleTest = ValidProcedureTrilogyTest("bar", argumentTable, emptyList())
                testSubjectCallerStub.resultToReturn = emptyMap()
                val expectedError = "Row 1 of 1: Expected any error to occur, but no errors were triggered"
                val testCase = ProcedureTrilogyTestCase("someOtherProcedure", "d.e.s.c.r.i.p.t.i.o.n", listOf(singleTest), testCaseHooks)
                expect(expectedError) { testCaseRunner.run(testCase, FixtureLibrary.emptyLibrary()).tests.first().errorMessage }
            }
        }

        context("when an error is thrown during execution") {
            val testCase = ProcedureTrilogyTestCase("someProcedure", "someDescription", Fixtures.buildSingleTest(), testCaseHooks)
            beforeEach { testSubjectCallerStub.exceptionToThrow = InputArgumentException("boo", RuntimeException("boom!")) }

            it("should fail to run the test case") {
                expect(false) { testCaseRunner.run(testCase, fixtureLibrary).didPass }
            }

            it("should include the error message in the test case result") {
                testCaseRunner.run(testCase, fixtureLibrary).failedTests.first().errorMessage!! shouldContain "boo"
            }

            it("should include the table row number, and total number of rows in the error message") {
                testCaseRunner.run(testCase, fixtureLibrary).failedTests.first().errorMessage!! shouldContain "Row 1 of 1: "
            }
        }

        context("when a malformed test is present") {
            val testCase = ProcedureTrilogyTestCase("someProcedure", "some description",
                    Fixtures.buildSingleMalformedTest(), hooksWithBeforeAllAndBeforeEachTest)

            it("should fail the test case") {
                expect(false) { testCaseRunner.run(testCase, fixtureLibrary).didPass }
            }

            it("should include the reason for failure") {
                val result = testCaseRunner.run(testCase, fixtureLibrary)
                expect("Belay, misty shipmate.") { result.failedTests.first().errorMessage }
            }

            it("should give the failed test name") {
                val result = testCaseRunner.run(testCase, fixtureLibrary)
                expect("Warm rice quickly.") { result.failedTests.first().testName }
            }
        }
    }
})
