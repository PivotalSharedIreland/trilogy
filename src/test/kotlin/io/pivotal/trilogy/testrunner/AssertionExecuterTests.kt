package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.DatabaseHelper
import io.pivotal.trilogy.testcase.TrilogyAssertion
import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class AssertionExecuterTests : Spek ({
    describe("executing assertions") {
        it("returns true when the assertion does not raise an error") {
            val executer = AssertionExecuter(DatabaseHelper.dataSource())
            val sql = "BEGIN NULL; END;"
            expect(true) { executer.execute(TrilogyAssertion("", sql)) }
        }

        it("returns false when the assertion raises an error") {
            val executer = AssertionExecuter(DatabaseHelper.dataSource())
            val sql = "BEGIN RAISE_APPLICATION_ERROR(-20000, 'Oops'); END;"
            expect(false) { executer.execute(TrilogyAssertion("", sql)) }
        }
    }
})