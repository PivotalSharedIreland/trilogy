package io.pivotal.trilogy

import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

object DatabaseHelper {
    val jdbcUrl = "jdbc:oracle:thin:@192.168.99.100:32769:xe"

    fun dataSource(): DataSource {
        return DriverManagerDataSource().apply {
            setDriverClassName("oracle.jdbc.driver.OracleDriver")
            url = jdbcUrl
            username = "APP_USER"
            password = "secret"
        }
    }

}