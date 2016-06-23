package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TrilogyAssertion
import org.springframework.core.NestedRuntimeException
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.SQLException

class AssertionExecutor(val jdbcTemplate : JdbcTemplate) : IAssertionExecutor {

    override infix fun execute(assertion: TrilogyAssertion): Boolean {
        try {
            jdbcTemplate.execute(assertion.body)
        } catch(e: NestedRuntimeException) {
            if (e.contains(SQLException::class.java)) {
                return false
            }
        }

        return true
    }

}
