package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.ResourceHelper
import io.pivotal.trilogy.testcase.ProcedureTrilogyTest
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect


class ProcedureStringTestParserTests : Spek({
    context("minimal") {
        val testString = ResourceHelper.getTestByName("minimal")
        val firstRow = listOf("FOO", "12", "")
        val secondRow = listOf("__NULL__", "0", "")
        val thirdRow = listOf("BAR", "-18", "")
        val fourthRow = listOf("", "12", "")
        val dataTable = listOf(firstRow, secondRow, thirdRow, fourthRow)
        val inputTable = dataTable.map { row -> listOf(row[0], row[1]) }

        it("can be read") {
            ProcedureStringTestParser(testString)
        }

        it("ignores leading whitespace") {
            ProcedureStringTestParser("     \n\n  \n$testString")
        }

        it("fails if the test header is not the first") {
            assertFails { ProcedureStringTestParser("foo\n\n\n    \n$testString").getTest() }
        }

        it("reads the test description") {
            expect("Test description") { ProcedureStringTestParser(testString).getTest().description }
        }

        it("reads the execution table headers") {
            expect(listOf("PARAM1", "PARAM2")) { (ProcedureStringTestParser(testString).getTest() as ProcedureTrilogyTest).argumentTable.inputArgumentNames }
        }

        it("reads the execution table values") {
            val parsedTest = ProcedureStringTestParser(testString).getTest() as ProcedureTrilogyTest
            expect(inputTable) { parsedTest.argumentTable.inputArgumentValues }
            expect(listOf(emptyList(), emptyList(), emptyList(), emptyList())) { parsedTest.argumentTable.outputArgumentValues }
        }

        it("ignores leading spaces in table definition") {
            ProcedureStringTestParser(testString.replace(Regex("^\\|"), "   |"))
        }

        it("returns a test with an empty assertion list when it is absent") {
            expect(emptyList()) { ProcedureStringTestParser(testString).getTest().assertions }
        }
    }
})