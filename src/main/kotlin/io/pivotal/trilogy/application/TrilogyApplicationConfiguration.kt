package io.pivotal.trilogy.application

import io.pivotal.trilogy.testrunner.AssertionExecuter
import io.pivotal.trilogy.testrunner.DatabaseAssertionExecuter
import io.pivotal.trilogy.testrunner.DatabaseScriptExecuter
import io.pivotal.trilogy.testrunner.DatabaseTestCaseRunner
import io.pivotal.trilogy.testrunner.DatabaseTestProjectRunner
import io.pivotal.trilogy.testrunner.DatabaseProcedureCaller
import io.pivotal.trilogy.testrunner.ScriptExecuter
import io.pivotal.trilogy.testrunner.TestCaseRunner
import io.pivotal.trilogy.testrunner.TestProjectRunner
import io.pivotal.trilogy.testrunner.TestSubjectCaller
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
open class TrilogyApplicationConfiguration {
    @Bean
    open fun testSubjectCaller(dataSource: DataSource): TestSubjectCaller {
        return DatabaseProcedureCaller(dataSource)
    }

    @Bean
    open fun scriptExecuter(jdbcTemplate: JdbcTemplate): ScriptExecuter {
        return DatabaseScriptExecuter(jdbcTemplate)
    }

    @Bean
    open fun assertionExecuter(scriptExecuter: ScriptExecuter): AssertionExecuter {
        return DatabaseAssertionExecuter(scriptExecuter)
    }

    @Bean
    open fun testCaseRunner(testSubjectCaller : TestSubjectCaller, assertionExecuter: AssertionExecuter, scriptExecuter: ScriptExecuter) : TestCaseRunner {
        return DatabaseTestCaseRunner(testSubjectCaller, assertionExecuter, scriptExecuter)
    }

    @Bean
    open fun testProjectRunner(testCaseRunner : TestCaseRunner, scriptExecuter : ScriptExecuter) : TestProjectRunner {
        return DatabaseTestProjectRunner(testCaseRunner, scriptExecuter)
    }
}
