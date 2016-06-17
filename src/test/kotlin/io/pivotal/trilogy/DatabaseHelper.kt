package io.pivotal.trilogy

import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

object DatabaseHelper {
    fun dataSource(): DataSource {
        return DriverManagerDataSource().apply {
            setDriverClassName("oracle.jdbc.driver.OracleDriver")
            url = "jdbc:oracle:thin:@192.168.99.100:32769:xe"
            username = "APP_USER"
            password = "secret"
        }
    }
}