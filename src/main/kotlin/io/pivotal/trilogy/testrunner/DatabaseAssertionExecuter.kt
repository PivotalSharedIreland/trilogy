package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TrilogyAssertion
import org.springframework.core.NestedRuntimeException
import java.sql.SQLException

class DatabaseAssertionExecuter(val scriptExecuter: ScriptExecuter) : AssertionExecuter {

    override infix fun execute(assertion: TrilogyAssertion): Boolean {
        try {
            scriptExecuter.execute(assertion.body)
        } catch(e: NestedRuntimeException) {
            if (e.contains(SQLException::class.java)) {
                return false
            }
        }

        return true
    }

}
