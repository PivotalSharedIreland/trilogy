package io.pivotal.trilogy

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

object DatabaseHelper {

    val oracleJdbcUrl = "jdbc:oracle:thin:@192.168.99.100:32769:xe"
    val pgJdbcUrl = "jdbc:postgresql://localhost:5432/pivotal"

    fun oracleDataSource(): DataSource {
        return DriverManagerDataSource().apply {
            setDriverClassName("oracle.jdbc.driver.OracleDriver")
            url = oracleJdbcUrl
            username = "app_user"
            password = "secret"
        }
    }

    fun jdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(oracleDataSource())
    }

    fun executeScript(scriptName: String) {
        jdbcTemplate().execute(ResourceHelper.getScriptByName(scriptName))
    }

    fun pgDataSource(): DataSource {
        return DriverManagerDataSource().apply {
            setDriverClassName("org.postgresql.Driver")
            url = pgJdbcUrl
            username = "pivotal"
            password = ""
        }
    }

}
