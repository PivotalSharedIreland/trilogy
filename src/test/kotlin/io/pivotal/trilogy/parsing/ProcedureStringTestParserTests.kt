package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.parsing.exceptions.MalformedDataSection
import io.pivotal.trilogy.parsing.exceptions.MissingDataSection
import io.pivotal.trilogy.test_helpers.ResourceHelper
import io.pivotal.trilogy.test_helpers.shouldThrow
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
            expect(listOf("PARAM1", "PARAM2")) { (ProcedureStringTestParser(testString).getTest()).argumentTable.inputArgumentNames }
        }

        it("reads the execution table values") {
            val parsedTest = ProcedureStringTestParser(testString).getTest()
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

    context("with SQL assertion") {
        val testString = ResourceHelper.getTestByName("sqlAssertion")
        val sqlStatement = "DECLARE\n" +
                "    l_count NUMBER;\n" +
                "    wrong_count EXCEPTION;\n" +
                "BEGIN\n" +
                "    SELECT count(*) INTO l_count FROM dual;\n" +
                "    IF l_count = 0\n" +
                "    THEN\n" +
                "        RAISE wrong_count;\n" +
                "    END IF;\n" +
                "END;"

        it("reads assertion description") {
            expect("Assertion description") { ProcedureStringTestParser(testString).getTest().assertions[0].description }
        }

        it("maintains the argument table size") {
            expect(4) { (ProcedureStringTestParser(testString).getTest()).argumentTable.inputArgumentValues.count() }
        }

        it("reads assertion body") {
            expect(sqlStatement) { ProcedureStringTestParser(testString).getTest().assertions[0].body }
        }

        context("multiple assertions") {
            val secondSqlStatement = sqlStatement.replace("l_count", "alt_count")
            val assertions = ProcedureStringTestParser(ResourceHelper.getTestByName("multipleSqlAssertions")).getTest().assertions

            it("reads all assertions") {
                expect(2) { assertions.count() }
                expect(sqlStatement) { assertions.first().body }
                expect(secondSqlStatement) { assertions.last().body }
            }
        }
    }

    it("fails for an empty string") {
        assertFails { ProcedureStringTestParser("").getTest() }
    }

    it("fails for a test without a body") {
        assertFails { ProcedureStringTestParser("## TEST\nAll sea-dogs hail cold, coal-black reefs.").getTest() }
    }

    it("fails for empty test description") {
        assertFails { ProcedureStringTestParser(ResourceHelper.getTestByName("emptyDescription")).getTest() }
    }

    context("procedural") {
        it("fails when data section is missing") {
            { ProcedureStringTestParser("## TEST\nPathways fly on wind at earth!").getTest() } shouldThrow MissingDataSection::class
        }

        it("fails when data section is malformed") {
            { ProcedureStringTestParser("## TEST\nPathways fly on wind at earth!\n### DATA\nHubba bubba").getTest() } shouldThrow MalformedDataSection::class
        }
    }
})