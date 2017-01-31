package io.pivotal.trilogy.live.oracle

import io.pivotal.trilogy.test_helpers.DatabaseHelper
import io.pivotal.trilogy.test_helpers.shouldContain
import io.pivotal.trilogy.test_helpers.shouldStartWith
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testrunner.DatabaseAssertionExecuter
import io.pivotal.trilogy.testrunner.DatabaseScriptExecuter
import org.jetbrains.spek.api.Spek
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.expect

class AssertionExecuterTests : Spek({

    describe("executing assertions") {

        val jdbcTemplate = JdbcTemplate(DatabaseHelper.oracleDataSource())
        val scriptExecuter = DatabaseScriptExecuter(jdbcTemplate)

        it("returns null when the assertion does not raise an error") {
            val executer = DatabaseAssertionExecuter(scriptExecuter)
            val sql = "BEGIN NULL; END;"
            expect(null) { executer.executeReturningFailureMessage(TrilogyAssertion("", sql)) }
        }

        it("returns failure message when the assertion raises an error") {
            val executer = DatabaseAssertionExecuter(scriptExecuter)
            val sql = "BEGIN RAISE_APPLICATION_ERROR(-20000, 'Oops'); END;"
            val result = executer.executeReturningFailureMessage(TrilogyAssertion("My assertion", sql))
            result!! shouldStartWith "Assertion failure: My assertion\n"
            result shouldContain "Oops"
        }
    }
})