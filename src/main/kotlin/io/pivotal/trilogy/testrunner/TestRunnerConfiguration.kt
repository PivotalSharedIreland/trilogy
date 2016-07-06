package io.pivotal.trilogy.testrunner

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
open class TestRunnerConfiguration {
    @Bean
    open fun testSubjectCaller(dataSource: DataSource): TestSubjectCaller {
        return DatabaseTestSubjectCaller(dataSource)
    }

    @Bean
    open fun assertionExecutor(jdbcTemplate: JdbcTemplate): AssertionExecutor {
        return DatabaseAssertionExecutor(jdbcTemplate)
    }

    @Bean
    open fun scriptExecuter(jdbcTemplate: JdbcTemplate): ScriptExecuter {
        return DatabaseScriptExecuter(jdbcTemplate)
    }

    @Bean
    open fun testCaseRunner(testSubjectCaller : TestSubjectCaller, assertionExecutor: AssertionExecutor, scriptExecuter: ScriptExecuter) : TestCaseRunner {
        return DatabaseTestCaseRunner(testSubjectCaller, assertionExecutor, scriptExecuter)
    }

    @Bean
    open fun testProjectRunner(testCaseRunner : TestCaseRunner, scriptExecuter : ScriptExecuter) : TestProjectRunner {
        return DatabaseTestProjectRunner(testCaseRunner, scriptExecuter)
    }
}
