package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.mocks.ScriptExecuterSpy
import io.pivotal.trilogy.mocks.TestCaseRunnerSpy
import io.pivotal.trilogy.mocks.TrilogyApplicationOptionsStub
import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.shouldStartWith
import io.pivotal.trilogy.testcase.ProcedureTrilogyTestCase
import io.pivotal.trilogy.testproject.TestProjectBuilder
import io.pivotal.trilogy.testproject.TrilogyTestProject
import io.pivotal.trilogy.testproject.UrlTestProjectResourceLocator
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.expect

class DatabaseTestProjectRunnerTests : Spek({

    val projectRoot = "src/test/resources/projects/"

    fun projectNamed(name: String): TrilogyTestProject {
        val projectUrl = File("$projectRoot$name").toURI().toURL()
        val options = TrilogyApplicationOptionsStub()
        options.locator = UrlTestProjectResourceLocator(projectUrl)
        return TestProjectBuilder.build(options)
    }

    it("runs the tests for a simple project") {
        val mockTestCaseRunner = TestCaseRunnerSpy()
        val project = projectNamed("simple")
        DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterSpy()).run(project)
        expect(1) { mockTestCaseRunner.runCount }
        expect("EXAMPLE_PROCEDURE") {
            (mockTestCaseRunner.runArgument as? ProcedureTrilogyTestCase)?.procedureName
        }
        expect("Example") { mockTestCaseRunner.runArgument?.description }
        expect(2) { mockTestCaseRunner.runArgument?.tests?.count() }
    }

    describe("multiple test cases in a project") {
        val project = projectNamed("multiple_testcases")

        it("runs all test cases") {
            val mockTestCaseRunner = TestCaseRunnerSpy()
            DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterSpy()).run(project)
            expect(2) { mockTestCaseRunner.runCount }
        }

        it("summarizes the results") {
            val mockTestCaseRunner = TestCaseRunnerSpy().apply { runResult = TestCaseResult(2, 3) }
            val testProjectResult = DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterSpy()).run(project)
            expect(TestCaseResult(4, 6)) { testProjectResult }
        }

        it("executes scripts from the src directory") {
            val scriptExecuterSpy = ScriptExecuterSpy()
            DatabaseTestProjectRunner(TestCaseRunnerSpy(), scriptExecuterSpy).run(project)
            expect(2) { scriptExecuterSpy.executeCalls }
        }

        it("executes scripts in the right order") {
            val scriptExecuterSpy = ScriptExecuterSpy()
            DatabaseTestProjectRunner(TestCaseRunnerSpy(), scriptExecuterSpy).run(project)
            scriptExecuterSpy.executeArgList.first() shouldStartWith "CREATE OR REPLACE PROCEDURE EXAMPLE$"
            scriptExecuterSpy.executeArgList.last() shouldStartWith "CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE"
        }
    }

    describe("test cases with schema definition") {
        val project = projectNamed("schema")

        it("excludes fixtures from test case file list") {
            val testCaseRunner = TestCaseRunnerSpy()
            DatabaseTestProjectRunner(testCaseRunner, ScriptExecuterSpy()).run(project)
            expect(1) { testCaseRunner.runCount }
        }

        it("loads schema before tests") {
            val scriptExecuterSpy = ScriptExecuterSpy()
            DatabaseTestProjectRunner(TestCaseRunnerSpy(), scriptExecuterSpy).run(project)
            scriptExecuterSpy.executeArgList.first() shouldStartWith "CREATE TABLE CLIENTS"
        }
    }

})

