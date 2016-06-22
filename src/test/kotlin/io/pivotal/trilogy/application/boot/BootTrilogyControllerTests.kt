package io.pivotal.trilogy.application.boot

import io.pivotal.trilogy.DatabaseHelper
import io.pivotal.trilogy.application.TrilogyApplicationOptions
import io.pivotal.trilogy.testrunner.AssertionExecutor
import io.pivotal.trilogy.testrunner.TestSubjectCaller
import org.jetbrains.spek.api.Spek
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import kotlin.test.expect

class BootTrilogyControllerTests : Spek ({

    fun bootTrilogyController(): BootTrilogyController {
        val controller = BootTrilogyController()
        val dataSource = DatabaseHelper.dataSource()
        val jdbcTemplate = JdbcTemplate(dataSource)
        val simpleJdbcCall = SimpleJdbcCall(dataSource)
        controller.assertionExecutor = AssertionExecutor(jdbcTemplate)
        controller.testSubjectCaller = TestSubjectCaller(simpleJdbcCall)
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
    }

})
