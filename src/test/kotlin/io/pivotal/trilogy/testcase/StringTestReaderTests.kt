package io.pivotal.trilogy.testcase

import io.pivotal.trilogy.ResourceHelper
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect

class StringTestReaderTests : Spek({
    context("minimal") {
        val testString = ResourceHelper.getTestByName("minimal")
        val firstRow = listOf("FOO", "12", "")
        val secondRow = listOf("__NULL__", "0", "")
        val thirdRow = listOf("BAR", "-18", "")
        val fourthRow = listOf("", "12", "")
        val dataTable = listOf(firstRow, secondRow, thirdRow, fourthRow)

        it("can be read") {
            StringTestReader(testString)
        }

        it("ignores leading whitespace") {
            StringTestReader("     \n\n  \n$testString")
        }

        it("fails if the test header is not the first") {
            assertFails { StringTestReader("foo\n\n\n    \n$testString") }
        }

        it("reads the test description") {
            expect("Test description") { StringTestReader(testString).getTest().description }
        }

        it("reads the execution table headers") {
            expect(listOf("PARAM1", "PARAM2", "=ERROR=")) { StringTestReader(testString).getTest().argumentTable.labels }
        }

        it("reads the execution table values") {
            expect(dataTable) { StringTestReader(testString).getTest().argumentTable.values }
        }

        it("ignores leading spaces in table definition") {
            StringTestReader(testString.replace(Regex("^\\|"), "   |"))
        }

        it("returns a test with an empty assertion list when it is absent") {
            expect(emptyList()) { StringTestReader(testString).getTest().assertions }
        }
    }

    context("with SQL assertion") {
        val testString = ResourceHelper.getTestByName("sqlAssertion")

        it("reads assertion description") {
            expect("Assertion description") { StringTestReader(testString).getTest().assertions[0].description }
        }

        it("maintains the argument table size") {
            expect(4) { StringTestReader(testString).getTest().argumentTable.values.count() }
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
            expect(sqlStatement) { StringTestReader(testString).getTest().assertions[0].body }
        }
    }

    it("fails for an empty string") {
        assertFails { StringTestReader("") }
    }

    it("fails for a test without a data section") {
        assertFails { StringTestReader("## TEST\nAll sea-dogs hail cold, coal-black reefs.") }
    }

    it("fails for empty test description") {
        assertFails { StringTestReader(ResourceHelper.getTestByName("emptyDescription")).getTest() }
    }
})