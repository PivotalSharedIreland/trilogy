package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.mocks.ScriptExecuterMock
import io.pivotal.trilogy.mocks.TestCaseRunnerMock
import io.pivotal.trilogy.mocks.TrilogyApplicationOptionsStub
import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.reporting.TestResult
import io.pivotal.trilogy.test_helpers.shouldStartWith
import io.pivotal.trilogy.test_helpers.shouldThrow
import io.pivotal.trilogy.test_helpers.timesRepeat
import io.pivotal.trilogy.testcase.ProcedureTrilogyTestCase
import io.pivotal.trilogy.testproject.TestProjectBuilder
import io.pivotal.trilogy.testproject.TrilogyTestProject
import io.pivotal.trilogy.testproject.UrlTestProjectResourceLocator
import org.amshove.kluent.AnyException
import org.jetbrains.spek.api.Spek
import org.springframework.jdbc.BadSqlGrammarException
import java.io.File
import java.sql.SQLException
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
        val mockTestCaseRunner = TestCaseRunnerMock()
        val project = projectNamed("simple")
        DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterMock()).run(project)
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
            val mockTestCaseRunner = TestCaseRunnerMock()
            DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterMock()).run(project)
            expect(2) { mockTestCaseRunner.runCount }
        }

        it("summarizes the results") {
            val passedTestResult = TestResult("Passed")
            val failedTestResult = TestResult("Failed", "Failure message")
            val singleRunResult = 2.timesRepeat { passedTestResult } + 3.timesRepeat { failedTestResult }
            val expectedResult = 2.timesRepeat { TestCaseResult("", singleRunResult) }
            val mockTestCaseRunner = TestCaseRunnerMock().apply {
                runResult = TestCaseResult("", singleRunResult)
            }
            val testProjectResult = DatabaseTestProjectRunner(mockTestCaseRunner, ScriptExecuterMock()).run(project)
            expect(expectedResult) { testProjectResult.testCaseResults }
        }

        it("executes scripts from the src directory") {
            val scriptExecuterMock = ScriptExecuterMock()
            DatabaseTestProjectRunner(TestCaseRunnerMock(), scriptExecuterMock).run(project)
            expect(2) { scriptExecuterMock.executeCalls }
        }

        it("executes scripts in the right order") {
            val scriptExecuterMock = ScriptExecuterMock()
            DatabaseTestProjectRunner(TestCaseRunnerMock(), scriptExecuterMock).run(project)
            scriptExecuterMock.executeArgList.first() shouldStartWith "CREATE OR REPLACE PROCEDURE EXAMPLE$"
            scriptExecuterMock.executeArgList.last() shouldStartWith "CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE"
        }
    }

    describe("test project with schema definition") {
        val project = projectNamed("schema")

        it("excludes fixtures from test case file list") {
            val testCaseRunner = TestCaseRunnerMock()
            DatabaseTestProjectRunner(testCaseRunner, ScriptExecuterMock()).run(project)
            expect(1) { testCaseRunner.runCount }
        }

        it("loads schema before tests") {
            val scriptExecuterMock = ScriptExecuterMock()
            DatabaseTestProjectRunner(TestCaseRunnerMock(), scriptExecuterMock).run(project)
            scriptExecuterMock.executeArgList.first() shouldStartWith "CREATE TABLE CLIENTS"
        }

        it("stops on failed schema load") {
            val scriptExecuterMock = ScriptExecuterMock()
            scriptExecuterMock.shouldFailExecution = true
            { DatabaseTestProjectRunner(TestCaseRunnerMock(), scriptExecuterMock).run(project) } shouldThrow SchemaLoadFailedException::class
        }

        it("describes the schema load error") {
            val scriptExecutorMock = ScriptExecuterMock()
            scriptExecutorMock.shouldFailExecution = true
            scriptExecutorMock.failureException = RuntimeException("The transformator\nis quickly seismic.")
            val exception = try {
                DatabaseTestProjectRunner(TestCaseRunnerMock(), scriptExecutorMock).run(project)
                null
            } catch (e: SchemaLoadFailedException) {
                e
            }

            val error = "Unable to load schema:\n    The transformator\n    is quickly seismic."
            expect(error) { exception!!.localizedMessage }
        }
    }

    describe("test case with broken source scripts") {
        val project = projectNamed("broken_source")

        it("raises an inconsistency error") {
            val scriptExecuterMock = ScriptExecuterMock()
            scriptExecuterMock.shouldFailExecution = true
            scriptExecuterMock.failureException = BadSqlGrammarException("Uhm...", "what?", SQLException("Bang!"))
            val runner = DatabaseTestProjectRunner(TestCaseRunnerMock(), scriptExecuterMock);
            { runner.run(project) } shouldThrow SourceScriptLoadException::class
        }
    }

})

