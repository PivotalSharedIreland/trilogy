package io.pivotal.trilogy.testrunner


import io.pivotal.trilogy.DatabaseHelper
import org.jetbrains.spek.api.Spek
import java.math.BigDecimal
import kotlin.test.expect

class DatabaseProcedureCallerTests : Spek({
    context("degenerate") {
        DatabaseHelper.executeScript("degenerate")

        it("returns the result of the execution") {
            val returnValue = mapOf(Pair("V_OUT", BigDecimal.ONE))
            val actualReturnValue = DatabaseProcedureCaller(DatabaseHelper.oracleDataSource()).call("degenerate", listOf("V_IN"), listOf("1"))
            expect(returnValue) { actualReturnValue }
        }

        it("accepts NULL values as arguments") {
            val returnValue = mapOf(Pair("V_OUT", null))
            val actualReturnValue = DatabaseProcedureCaller(DatabaseHelper.oracleDataSource()).call("degenerate", listOf("V_IN"), listOf("__NULL__"))
            expect(returnValue) { actualReturnValue }
        }
    }

    context("errors") {
        DatabaseHelper.executeScript("errors")

        it("returns the error description") {
            val returnValue = mapOf(Pair("=ERROR=", "ORA-20111: An error has occurred\nORA-06512: at \"APP_USER.ERRORS\", line 4\nORA-06512: at line 1\n"))
            expect(returnValue) { DatabaseProcedureCaller(DatabaseHelper.oracleDataSource()).call("errors", listOf("V_IN", "=ERROR="), listOf("101", "ANY")) }
        }
    }

})
