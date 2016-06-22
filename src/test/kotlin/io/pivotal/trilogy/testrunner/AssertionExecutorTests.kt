package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.DatabaseHelper
import io.pivotal.trilogy.testcase.TrilogyAssertion
import org.jetbrains.spek.api.Spek
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.expect

class AssertionExecutorTests : Spek ({

    describe("executing assertions") {

        val jdbcTemplate = JdbcTemplate(DatabaseHelper.dataSource())

        it("returns true when the assertion does not raise an error") {
            val executer = AssertionExecutor(jdbcTemplate)
            val sql = "BEGIN NULL; END;"
            expect(true) { executer.execute(TrilogyAssertion("", sql)) }
        }

        it("returns false when the assertion raises an error") {
            val executer = AssertionExecutor(jdbcTemplate)
            val sql = "BEGIN RAISE_APPLICATION_ERROR(-20000, 'Oops'); END;"
            expect(false) { executer.execute(TrilogyAssertion("", sql)) }
        }
    }
})
