package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.mocks.ScriptExecuterMock
import io.pivotal.trilogy.testcase.TrilogyAssertion
import org.jetbrains.spek.api.Spek
import org.springframework.jdbc.UncategorizedSQLException
import java.sql.SQLDataException
import kotlin.test.expect

class DatabaseAssertionExecuterTest : Spek({
    val scriptExecuter = ScriptExecuterMock()
    val assertion = TrilogyAssertion("foo", "blue")
    var subject = DatabaseAssertionExecuter(scriptExecuter)

    beforeEach {
        subject = DatabaseAssertionExecuter(scriptExecuter)
    }

    it("returns nil when the assertion succeeds") {
        val result = subject.executeReturningFailureMessage(assertion)
        expect(null) { result }
    }

    it("returns the enclosed exception message when the assertion fails") {
        scriptExecuter.failureException = UncategorizedSQLException("Super heroic", "Oops", SQLDataException("Ouch!"))
        scriptExecuter.shouldFailExecution = true
        val result = subject.executeReturningFailureMessage(assertion)
        expect("Assertion failure: foo\n    Ouch!") { result }
    }
})