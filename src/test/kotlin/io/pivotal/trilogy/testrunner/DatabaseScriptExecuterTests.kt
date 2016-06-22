package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.DatabaseHelper
import org.amshove.kluent.AnyException
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek

class DatabaseScriptExecuterTests : Spek({
    describe("Live connection") {
        val subject = DatabaseScriptExecuter(DatabaseHelper.jdbcTemplate())

        it("executes correct scripts successfully") {
            subject.execute("BEGIN NULL; END;")
        }

        it("throws an exception when the script contains an error") {
            { subject.execute("Per guest prepare one quarter") } shouldThrow AnyException
        }
    }
})