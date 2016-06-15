package io.pivotal.trilogy.testrunner


import org.amshove.kluent.Verify
import org.amshove.kluent.VerifyNoFurtherInteractions
import org.amshove.kluent.When
import org.amshove.kluent.called
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.mock
import org.amshove.kluent.on
import org.amshove.kluent.that
import org.amshove.kluent.was
import org.jetbrains.spek.api.Spek
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import java.math.BigInteger
import kotlin.test.expect

class TestSubjectCallerTests : Spek ({

    it("sets function name on init") {
        val mockCall = mock(SimpleJdbcCall::class)
        TestSubjectCaller(mockCall, "foo", emptyList())
        Verify on mockCall that mockCall.withProcedureName("foo") was called
        VerifyNoFurtherInteractions on mockCall
    }

    it("places the arguments correctly") {
        val mockCall = mock(SimpleJdbcCall::class)
        val expectedCallArguments = mapOf(Pair("BAR", "1"), Pair("BAZ", "2"))

        TestSubjectCaller(mockCall, "foo", listOf("BAR", "BAZ")).call(listOf("1", "2"))
        Verify on mockCall that mockCall.execute(expectedCallArguments) was called
    }

    it("returns the result of the execution") {
        val mockCall = mock(SimpleJdbcCall::class)
        val expectedCallArguments = mapOf(Pair("BAR", "1"), Pair("BAZ", "2"))
        val returnValue = mapOf(Pair("JOE", BigInteger.ONE), Pair("BEN", BigInteger.TEN))
        When calling  mockCall.execute(expectedCallArguments) itReturns returnValue

        expect(returnValue) { TestSubjectCaller(mockCall, "foo", listOf("BAR", "BAZ")).call(listOf("1", "2")) }
    }


})