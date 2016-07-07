package io.pivotal.trilogy

import io.pivotal.trilogy.testcase.StringTestCaseParser
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTest
import io.pivotal.trilogy.testcase.TrilogyTestCase

object Fixtures {
    val argumentTable = TestArgumentTable(listOf("IN", "OUT$"), listOf(listOf("1", "1")))
    val assertions = listOf(TrilogyAssertion("some description", "some SQL"))
    val testWithThreeRows: TrilogyTest by lazy {
        val dataRows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"))
        val dataLabels = listOf("A", "B", "C")
        val argumentTable = TestArgumentTable(dataLabels, dataRows)
        TrilogyTest("", argumentTable, emptyList())
    }

    fun getTestCase(testCaseName: String): TrilogyTestCase {
        return StringTestCaseParser(ResourceHelper.getTestCaseByName(testCaseName)).getTestCase()
    }

    fun buildSingleTest(): List<TrilogyTest> = listOf(TrilogyTest("I am a test", argumentTable, assertions))

    fun buildMultipleTests(): List<TrilogyTest> = listOf(
            TrilogyTest("I am a test", argumentTable, assertions),
            TrilogyTest("I am also a test", argumentTable, assertions),
            TrilogyTest("Me three", argumentTable, assertions)
    )
}
