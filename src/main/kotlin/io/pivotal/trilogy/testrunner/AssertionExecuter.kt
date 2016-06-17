package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TrilogyAssertion
import org.springframework.core.NestedRuntimeException
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.SQLException
import javax.sql.DataSource

class AssertionExecuter(val dataSource: DataSource) {
    infix fun execute(assertion: TrilogyAssertion): Boolean {
        try {
            JdbcTemplate(dataSource).execute(assertion.body)
        } catch(e: NestedRuntimeException) {
            if (e.contains(SQLException::class.java)) {
                return false
            }
        }

        return true
    }

}