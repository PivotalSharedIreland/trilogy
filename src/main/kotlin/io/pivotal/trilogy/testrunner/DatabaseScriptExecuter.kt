package io.pivotal.trilogy.testrunner

import org.springframework.jdbc.core.JdbcTemplate

class DatabaseScriptExecuter(val jdbcTemplate: JdbcTemplate) : ScriptExecuter {
    override fun execute(script: String) {
        jdbcTemplate.execute(script)
    }
}