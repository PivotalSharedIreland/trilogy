package io.pivotal.trilogy

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

object DatabaseHelper {

    val jdbcUrl = "jdbc:oracle:thin:@192.168.99.100:32769:xe"

    fun oracleDataSource(): DataSource {
        return DriverManagerDataSource().apply {
            setDriverClassName("oracle.jdbc.driver.OracleDriver")
            url = jdbcUrl
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

}
