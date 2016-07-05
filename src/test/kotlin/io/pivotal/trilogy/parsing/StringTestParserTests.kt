package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.ResourceHelper
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect

class StringTestParserTests : Spek({
    context("minimal") {
        val testString = ResourceHelper.getTestByName("minimal")
        val firstRow = listOf("FOO", "12", "")
        val secondRow = listOf("__NULL__", "0", "")
        val thirdRow = listOf("BAR", "-18", "")
        val fourthRow = listOf("", "12", "")
        val dataTable = listOf(firstRow, secondRow, thirdRow, fourthRow)
        val inputTable = dataTable.map { row -> listOf(row[0], row[1]) }

        it("can be read") {
            StringTestParser(testString)
        }

        it("ignores leading whitespace") {
            StringTestParser("     \n\n  \n$testString")
        }

        it("fails if the test header is not the first") {
            assertFails { StringTestParser("foo\n\n\n    \n$testString") }
        }

        it("reads the test description") {
            expect("Test description") { StringTestParser(testString).getTest().description }
        }

        it("reads the execution table headers") {
            expect(listOf("PARAM1", "PARAM2")) { StringTestParser(testString).getTest().argumentTable.inputArgumentNames }
        }

        it("reads the execution table values") {
            val parsedTest = StringTestParser(testString).getTest()
            expect(inputTable) { parsedTest.argumentTable.inputArgumentValues }
            expect(listOf(emptyList(), emptyList(), emptyList(), emptyList())) { parsedTest.argumentTable.outputArgumentValues }
        }

        it("ignores leading spaces in table definition") {
            StringTestParser(testString.replace(Regex("^\\|"), "   |"))
        }

        it("returns a test with an empty assertion list when it is absent") {
            expect(emptyList()) { StringTestParser(testString).getTest().assertions }
        }
    }

    context("with SQL assertion") {
        val testString = ResourceHelper.getTestByName("sqlAssertion")

        it("reads assertion description") {
            expect("Assertion description") { StringTestParser(testString).getTest().assertions[0].description }
        }

        it("maintains the argument table size") {
            expect(4) { StringTestParser(testString).getTest().argumentTable.inputArgumentValues.count() }
        }

        it("reads assertion body") {
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
            expect(sqlStatement) { StringTestParser(testString).getTest().assertions[0].body }
        }
    }

    it("fails for an empty string") {
        assertFails { StringTestParser("") }
    }

    it("fails for a test without a data section") {
        assertFails { StringTestParser("## TEST\nAll sea-dogs hail cold, coal-black reefs.") }
    }

    it("fails for empty test description") {
        assertFails { StringTestParser(ResourceHelper.getTestByName("emptyDescription")).getTest() }
    }
})