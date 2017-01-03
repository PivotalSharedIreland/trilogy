package io.pivotal.trilogy.live.oracle


import io.pivotal.trilogy.test_helpers.DatabaseHelper
import io.pivotal.trilogy.test_helpers.shouldContain
import io.pivotal.trilogy.test_helpers.shouldNotThrow
import io.pivotal.trilogy.test_helpers.shouldStartWith
import io.pivotal.trilogy.test_helpers.shouldThrow
import io.pivotal.trilogy.testrunner.DatabaseTestSubjectCaller
import io.pivotal.trilogy.testrunner.InputArgumentException
import org.amshove.kluent.AnyException
import org.jetbrains.spek.api.Spek
import java.math.BigDecimal
import kotlin.test.expect


class TestSubjectCallerTests : Spek({
    val dataSource = DatabaseHelper.oracleDataSource()
    context("degenerate") {

        it("returns the result of the execution") {
            val returnValue = mapOf("V_OUT" to BigDecimal.ONE)
            val actualReturnValue = DatabaseTestSubjectCaller(dataSource).call("degenerate", listOf("V_IN"), listOf("1"))
            expect(returnValue) { actualReturnValue }
        }

        it("accepts NULL values as arguments") {
            val returnValue = mapOf(Pair("V_OUT", null))
            val actualReturnValue = DatabaseTestSubjectCaller(dataSource).call("degenerate", listOf("V_IN"), listOf("__NULL__"))
            expect(returnValue) { actualReturnValue }
        }
    }

    context("all possible data types") {
        DatabaseHelper.executeScript("allInParamTypes")

        val fields = listOf("V_INT", "V_NUMBER", "V_TIMESTAMP", "V_DATE")
        var values: MutableList<String> = mutableListOf()

        beforeEach { values = mutableListOf("12345", "12345.67", "2020-12-31 12:59:59", "2007-05-12 22:33:44") }

        it("runs without errors") {
            { DatabaseTestSubjectCaller(dataSource).call("ALL_IN_PARAM_TYPES", fields, values) } shouldNotThrow AnyException

        }

        context("integer mismatch") {
            beforeEach { values[0] = "foo" }

            it("throws an error") {
                { DatabaseTestSubjectCaller(dataSource).call("ALL_IN_PARAM_TYPES", fields, values) } shouldThrow InputArgumentException::class
            }


            it("provides a message") {
                val error = try {
                    DatabaseTestSubjectCaller(dataSource).call("ALL_IN_PARAM_TYPES", fields, values)
                    InputArgumentException("", RuntimeException(""))
                } catch (e: InputArgumentException) {
                    e
                }
                val expectedMessageHeader = "Attempted to pass an incompatible value as a numeric parameter. " +
                        "Please review your inputs:\n"
                val actualMessage: String = error.message!!
                actualMessage shouldStartWith expectedMessageHeader
                actualMessage shouldContain "    V_INT => foo"
                actualMessage shouldContain "    V_NUMBER => 12345.67"
                actualMessage shouldContain "    V_TIMESTAMP => 2020-12-31 12:59:59"
                actualMessage shouldContain "    V_DATE => 2007-05-12 22:33:44"
            }
        }

        context("date mismatch") {
            beforeEach { values[2] = "ouch" }

            it("throws an error") {
                { DatabaseTestSubjectCaller(dataSource).call("ALL_IN_PARAM_TYPES", fields, values) } shouldThrow InputArgumentException::class
            }

            it("provides the error message") {
                val error = try {
                    DatabaseTestSubjectCaller(dataSource).call("ALL_IN_PARAM_TYPES", fields, values)
                    InputArgumentException("", RuntimeException(""))
                } catch (e: InputArgumentException) {
                    e
                }
                val expectedMessageHeader = "Attempted to pass an incompatible value as a date/time parameter. " +
                        "Please review your inputs:\n"
                val actualMessage: String = error.message!!
                actualMessage shouldStartWith expectedMessageHeader
                actualMessage shouldContain "    V_INT => 12345"
                actualMessage shouldContain "    V_NUMBER => 12345.67"
                actualMessage shouldContain "    V_TIMESTAMP => ouch"
                actualMessage shouldContain "    V_DATE => 2007-05-12 22:33:44"
                actualMessage shouldContain "Original error message: Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]"

            }
        }

    }

    context("errors") {
        DatabaseHelper.executeScript("errors")

        it("returns the error description") {
            val returnValue = mapOf(Pair("=ERROR=", "ORA-20111: An error has occurred\nORA-06512: at \"APP_USER.ERRORS\", line 4\nORA-06512: at line 1\n"))
            expect(returnValue) { DatabaseTestSubjectCaller(dataSource).call("errors", listOf("V_IN", "=ERROR="), listOf("101", "ANY")) }
        }
    }

})
