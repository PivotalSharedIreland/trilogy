package io.pivotal.trilogy.testrunner

import org.flywaydb.core.internal.dbsupport.DbSupportFactory
import org.flywaydb.core.internal.dbsupport.SqlScript
import org.springframework.jdbc.core.JdbcTemplate

class DatabaseScriptExecuter(val jdbcTemplate: JdbcTemplate) : ScriptExecuter {

    override fun execute(scriptBody: String) {
        val dbSupport = DbSupportFactory.createDbSupport(jdbcTemplate.dataSource.connection, false)
        val sqlScript = SqlScript(scriptBody, dbSupport)

        sqlScript.sqlStatements.forEach { statement ->
            jdbcTemplate.execute(statement.sql)
        }
    }

}