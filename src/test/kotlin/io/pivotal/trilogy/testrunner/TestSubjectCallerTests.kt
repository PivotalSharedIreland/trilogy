package io.pivotal.trilogy.testrunner


import io.pivotal.trilogy.DatabaseHelper
import org.jetbrains.spek.api.Spek
import java.math.BigDecimal
import kotlin.test.expect

class TestSubjectCallerTests : Spek ({

    it("returns the result of the execution") {
        val returnValue = mapOf(Pair("V_OUT", BigDecimal.ONE))
        val actualReturnValue = DatabaseTestSubjectCaller(DatabaseHelper.dataSource()).call("degenerate",listOf("V_IN"),listOf("1"))
        expect(returnValue) { actualReturnValue }
    }

    it("accepts NULL values as arguments") {
        val returnValue = mapOf(Pair("V_OUT", null))
        val actualReturnValue = DatabaseTestSubjectCaller(DatabaseHelper.dataSource()).call("degenerate", listOf("V_IN"),listOf("__NULL__"))
        expect(returnValue) { actualReturnValue }
    }

})
