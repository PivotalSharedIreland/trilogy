package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.ResourceHelper
import io.pivotal.trilogy.testcase.ProcedureTrilogyTest
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect

class StringTestParserTests : Spek({

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
            expect("Assertion description") { StringTestParser(testString).getTest().assertions[0].description }
        }

        it("maintains the argument table size") {
            expect(4) { (StringTestParser(testString).getTest() as ProcedureTrilogyTest).argumentTable.inputArgumentValues.count() }
        }

        it("reads assertion body") {
            expect(sqlStatement) { StringTestParser(testString).getTest().assertions[0].body }
        }

        context("multiple assertions") {
            val secondSqlStatement = sqlStatement.replace("l_count", "alt_count")
            val assertions = StringTestParser(ResourceHelper.getTestByName("multipleSqlAssertions")).getTest().assertions

            it("reads all assertions") {
                expect(2) { assertions.count() }
                expect(sqlStatement) { assertions.first().body }
                expect(secondSqlStatement) { assertions.last().body }
            }
        }
    }

    it("fails for an empty string") {
        assertFails { StringTestParser("").getTest() }
    }

    it("fails for a test without a body") {
        assertFails { StringTestParser("## TEST\nAll sea-dogs hail cold, coal-black reefs.").getTest() }
    }

    it("fails for empty test description") {
        assertFails { StringTestParser(ResourceHelper.getTestByName("emptyDescription")).getTest() }
    }
})