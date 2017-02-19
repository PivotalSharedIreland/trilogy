package io.pivotal.trilogy.test_helpers

import io.pivotal.trilogy.parsing.ProcedureStringTestCaseParser
import io.pivotal.trilogy.testcase.MalformedProcedureTrilogyTest
import io.pivotal.trilogy.testcase.ProcedureTrilogyTest
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testcase.ValidProcedureTrilogyTest

object Fixtures {
    val argumentTable = TestArgumentTable(listOf("IN", "OUT$"), listOf(listOf("1", "1")))
    val assertions = listOf(TrilogyAssertion("some description", "some SQL"))
    val testWithThreeRows: ProcedureTrilogyTest by lazy {
        ValidProcedureTrilogyTest("", argumentTableWithThreeRows, emptyList())
    }

    fun testWithThreeRowsAndAssertions(assertions: List<TrilogyAssertion>): ProcedureTrilogyTest {
        return ValidProcedureTrilogyTest("", argumentTableWithThreeRows, assertions)
    }

    private val argumentTableWithThreeRows: TestArgumentTable
        get() {
            val dataRows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"))
            val dataLabels = listOf("A", "B", "C")
            val argumentTable = TestArgumentTable(dataLabels, dataRows)
            return argumentTable
        }

    fun getTestCase(testCaseName: String): TrilogyTestCase {
        return ProcedureStringTestCaseParser(ResourceHelper.getTestCaseByName(testCaseName)).getTestCase()
    }

    fun buildSingleTest(): List<ProcedureTrilogyTest> = listOf(ValidProcedureTrilogyTest("I am a test", argumentTable, assertions))

    fun buildSingleMalformedTest(): List<ProcedureTrilogyTest> = listOf(
            MalformedProcedureTrilogyTest(errorMessage = "Belay, misty shipmate.", description = "Warm rice quickly."))

    fun buildMultipleTests(): List<ProcedureTrilogyTest> = listOf(
            ValidProcedureTrilogyTest("I am a test", argumentTable, assertions),
            ValidProcedureTrilogyTest("I am also a test", argumentTable, assertions),
            ValidProcedureTrilogyTest("Me three", argumentTable, assertions)
    )
}
