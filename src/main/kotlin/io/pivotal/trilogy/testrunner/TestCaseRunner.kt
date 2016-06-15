package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.validators.OutputArgumentValidator
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

class TestCaseRunner(val trilogyTestCase: TrilogyTestCase, val jdbcUrl: String) {

    fun run() : Boolean {
        val test = trilogyTestCase.tests.first()
        val testArgumentTable = test.argumentTable

        val testSubjectCaller = TestSubjectCaller(SimpleJdbcCall(getDataSource()), trilogyTestCase.functionName, testArgumentTable.inputArgumentNames)
        val outputValidator = OutputArgumentValidator(testArgumentTable.outputArgumentNames)

        return testArgumentTable.inputArgumentValues.mapIndexed { index, inputRow ->
            val output = testSubjectCaller.call(inputRow)
            outputValidator.validate(testArgumentTable.outputArgumentValues[index], output)
        }.fold(true, {a, b -> a and b})
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

