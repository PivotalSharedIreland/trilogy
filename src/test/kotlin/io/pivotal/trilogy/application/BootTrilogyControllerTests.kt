package io.pivotal.trilogy.application

import io.pivotal.trilogy.DatabaseHelper
import io.pivotal.trilogy.testrunner.AssertionExecutor
import io.pivotal.trilogy.testrunner.DatabaseScriptExecuter
import io.pivotal.trilogy.testrunner.DatabaseTestCaseRunner
import io.pivotal.trilogy.testrunner.DatabaseTestProjectRunner
import io.pivotal.trilogy.testrunner.TestSubjectCaller
import org.jetbrains.spek.api.Spek
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import kotlin.test.expect

class BootTrilogyControllerTests : Spek ({

    fun bootTrilogyController(): TrilogyController {
        val controller = TrilogyController()
        val dataSource = DatabaseHelper.dataSource()
        val jdbcTemplate = JdbcTemplate(dataSource)
        val simpleJdbcCall = SimpleJdbcCall(dataSource)
        val assertionExecutor = AssertionExecutor(jdbcTemplate)
        val testSubjectCaller = TestSubjectCaller(simpleJdbcCall)
        val testCaseRunner = DatabaseTestCaseRunner(testSubjectCaller, assertionExecutor)
        val scriptExecuter = DatabaseScriptExecuter(jdbcTemplate)
        controller.testCaseRunner = testCaseRunner
        controller.testProjectRunner = DatabaseTestProjectRunner(testCaseRunner, scriptExecuter)
        return controller
    }

    describe("execution") {
        val controller = bootTrilogyController()

        describe("simple cases") {
            it("succeeds for a simple case") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/should_pass.stt")
                expect(true) { controller.run(options).didPass }
            }

            it("fails for a simple case") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/should_fail.stt")
                expect(false) { controller.run(options).didPass }
            }
        }

        describe("tests with assertions") {
            it("succeed when the assertions pass") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/should_pass_with_sql.stt")
                expect(true) { controller.run(options).didPass }
            }

            it("fails when the assertions raise an error") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/should_fail_with_sql.stt")
                expect(false) { controller.run(options).didPass }
            }
        }

        describe("multiple tests in a test case") {
            it("succeeds when all the tests succeed") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/multiple/shouldPass.stt")
                expect(true) { controller.run(options).didPass }
            }

            it("fails when one of the tests is failing") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/multiple/shouldFail.stt")
                expect(false) { controller.run(options).didPass }
            }
        }

        describe("project") {
            beforeEach { DatabaseHelper.executeScript("simpleProjectCleanup") }
            it("passes for a simple project") {
                val options = TrilogyApplicationOptions(testProjectPath = "src/test/resources/projects/simple")
                val testCaseResult = controller.run(options)
                expect(true) { testCaseResult.didPass }
                expect(2) { testCaseResult.passed }
                expect(0) { testCaseResult.failed }
            }
        }

    }

})
