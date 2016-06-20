package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.validators.OutputArgumentValidator
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

class TestCaseRunner(val jdbcUrl: String) {
    fun run(trilogyTestCase: TrilogyTestCase): TestCaseResult {
        val stats = trilogyTestCase.tests.map { test ->
            runData(test.argumentTable, trilogyTestCase.functionName) and runAssertions(test.assertions)
        }

        val numberPassed = stats.filter { it }.size
        val numberFailed = stats.filterNot { it }.size

        return TestCaseResult(numberPassed, numberFailed)
    }

    private fun runAssertions(assertions: List<TrilogyAssertion>): Boolean {
        val assertionExecutor = AssertionExecuter(getDataSource())
        return assertions.all { assertion ->
            assertionExecutor execute assertion
        }
    }

    private fun runData(testArgumentTable: TestArgumentTable, functionName : String): Boolean {
        val testSubjectCaller = TestSubjectCaller(SimpleJdbcCall(getDataSource()), functionName, testArgumentTable.inputArgumentNames)
        val outputValidator = OutputArgumentValidator(testArgumentTable.outputArgumentNames)

        return testArgumentTable.inputArgumentValues.mapIndexed { index, inputRow ->
            val output = testSubjectCaller.call(inputRow)
            outputValidator.validate(testArgumentTable.outputArgumentValues[index], output)
        }.fold(true, { a, b -> a and b })
    }

    private fun getDataSource(): DataSource {
        return DriverManagerDataSource().apply {
            setDriverClassName("oracle.jdbc.driver.OracleDriver")
            url = jdbcUrl
            username = "APP_USER"
            password = "secret"
        }
    }
}

