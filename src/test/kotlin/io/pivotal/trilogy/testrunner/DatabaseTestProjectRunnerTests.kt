package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.mocks.ScriptExecuterSpy
import io.pivotal.trilogy.mocks.TestCaseRunnerSpy
import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.shouldStartWith
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.expect

class DatabaseTestProjectRunnerTests : Spek ({

    val projectRoot = "src/test/resources/projects/"

    it("returns success when there is no tests folder") {
        val projectUrl = File("${projectRoot}blank").toURI().toURL()
        val result = DatabaseTestProjectRunner(TestCaseRunnerSpy(), ScriptExecuterSpy()).run(projectUrl)
        expect(TestCaseResult(0, 0)) { result }
    }

    it("returns success when there is no test files in the test folder") {
        val projectUrl = File("${projectRoot}no_tests").toURI().toURL()
        val result = DatabaseTestProjectRunner(TestCaseRunnerSpy(), ScriptExecuterSpy()).run(projectUrl)
        expect(TestCaseResult(0, 0)) { result }
    }

    it("runs the tests for a simple project") {
        val mockTestCaseRunner = TestCaseRunnerSpy()
        val projectUrl = File("${projectRoot}simple").toURI().toURL()
        DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterSpy()).run(projectUrl)
        expect(1) { mockTestCaseRunner.runCount }
        expect("EXAMPLE_PROCEDURE") { mockTestCaseRunner.runArgument?.procedureName }
        expect("Example") { mockTestCaseRunner.runArgument?.description }
        expect(2) { mockTestCaseRunner.runArgument?.tests?.count() }
    }

    describe("multiple test cases in a project") {
        val projectUrl = File("${projectRoot}multiple_testcases").toURI().toURL()

        it("runs all test cases") {
            val mockTestCaseRunner = TestCaseRunnerSpy()
            DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterSpy()).run(projectUrl)
            expect(2) { mockTestCaseRunner.runCount }
        }

        it("summarizes the results") {
            val mockTestCaseRunner = TestCaseRunnerSpy().apply { runResult = TestCaseResult(2, 3) }
            val testProjectResult = DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterSpy()).run(projectUrl)
            expect(TestCaseResult(4, 6)) { testProjectResult }
        }

        it("executes scripts from the src directory") {
            val scriptExecuterSpy = ScriptExecuterSpy()
            DatabaseTestProjectRunner(TestCaseRunnerSpy(), scriptExecuterSpy).run(projectUrl)
            expect(2) { scriptExecuterSpy.executeCalls }
        }

        it("executes scripts in the right order") {
            val scriptExecuterSpy = ScriptExecuterSpy()
            DatabaseTestProjectRunner(TestCaseRunnerSpy(), scriptExecuterSpy).run(projectUrl)
            scriptExecuterSpy.executeArgList.first() shouldStartWith "CREATE OR REPLACE PROCEDURE EXAMPLE$"
            scriptExecuterSpy.executeArgList.last() shouldStartWith "CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE"
        }
    }

    describe("test cases with schema") {
        val projectUrl = File("${projectRoot}schema").toURI().toURL()

        it("excludes fixtures from test case file list") {
            val testCaseRunner = TestCaseRunnerSpy()
            DatabaseTestProjectRunner(testCaseRunner, ScriptExecuterSpy()).run(projectUrl)
            expect(1) { testCaseRunner.runCount }
        }

        it("loads schema before tests") {
            val scriptExecuterSpy = ScriptExecuterSpy()
            DatabaseTestProjectRunner(TestCaseRunnerSpy(), scriptExecuterSpy).run(projectUrl)
            scriptExecuterSpy.executeArgList.first() shouldStartWith "CREATE TABLE CLIENTS"
        }
    }

})

