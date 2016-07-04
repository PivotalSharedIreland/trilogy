package io.pivotal.trilogy

import io.pivotal.trilogy.testcase.StringTestCaseParser
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTest
import io.pivotal.trilogy.testcase.TrilogyTestCase

object Fixtures {
    val argumentTable = TestArgumentTable(listOf("IN", "OUT$"), listOf(listOf("1", "1")))
    val assertions = listOf(TrilogyAssertion("some description", "some SQL"))

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
