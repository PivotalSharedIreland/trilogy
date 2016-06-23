package io.pivotal.trilogy.testrunner

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import javax.sql.DataSource

@Configuration
open class TestRunnerConfiguration {

    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    open fun assertionExecutor(): AssertionExecutor {
        return AssertionExecutor(jdbcTemplate())
    }

    @Bean
    open fun simpleJdbcCall(): SimpleJdbcCall {
        return SimpleJdbcCall(dataSource)
    }

    @Bean
    open fun jdbcTemplate() : JdbcTemplate {
        return JdbcTemplate(dataSource)
    }

}
