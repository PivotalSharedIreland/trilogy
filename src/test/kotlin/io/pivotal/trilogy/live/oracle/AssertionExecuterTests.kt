package io.pivotal.trilogy.live.oracle

import io.pivotal.trilogy.test_helpers.DatabaseHelper
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

        it("returns true when the assertion does not raise an error") {
            val executer = DatabaseAssertionExecuter(scriptExecuter)
            val sql = "BEGIN NULL; END;"
            expect(true) { executer.execute(TrilogyAssertion("", sql)) }
        }

        it("returns false when the assertion raises an error") {
            val executer = DatabaseAssertionExecuter(scriptExecuter)
            val sql = "BEGIN RAISE_APPLICATION_ERROR(-20000, 'Oops'); END;"
            expect(false) { executer.execute(TrilogyAssertion("", sql)) }
        }
    }
})