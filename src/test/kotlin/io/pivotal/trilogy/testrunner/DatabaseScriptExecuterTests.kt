package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.DatabaseHelper
import org.amshove.kluent.AnyException
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek

class DatabaseScriptExecuterTests : Spek({

    describe("execute single statement with Live connection") {
        val subject = DatabaseScriptExecuter(DatabaseHelper.jdbcTemplate())

        it("executes correct scripts successfully") {
            subject.execute("BEGIN NULL; END;")
        }

        it("throws an exception when the script contains an error") {
            { subject.execute("Per guest prepare one quarter") } shouldThrow AnyException
        }
    }

    describe("execute multiple statement with Live connection") {
        val subject = DatabaseScriptExecuter(DatabaseHelper.jdbcTemplate())

        val statements = """BEGIN
            NULL;
            END;
            /
            BEGIN
            NULL;
            END;
            /"""

        it("executes multiple correct scripts successfully") {
            subject.execute(statements)
        }

        it("throws an exception when one statement contains an error") {
            { subject.execute(statements.plus("invalid sql statement")) } shouldThrow AnyException
        }
    }

})