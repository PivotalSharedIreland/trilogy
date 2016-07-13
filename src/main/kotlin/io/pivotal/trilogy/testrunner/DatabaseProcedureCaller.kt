package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TestArgumentTableTokens
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import java.util.*
import javax.sql.DataSource

class DatabaseProcedureCaller(@Autowired val dataSource: DataSource) : TestSubjectCaller {

    override fun call(procedureName: String, parameterNames: List<String>, parameterValues: List<String>): Map<String, Any?> {
        val jdbcCall = jdbcCall(procedureName)
        return jdbcCall.safeExecute(inputParameters(parameterNames, parameterValues))
    }

    private fun jdbcCall(procedureName: String): SimpleJdbcCall {
        return SimpleJdbcCall(dataSource).apply {
            withProcedureName(procedureName)
        }
    }

    private fun inputParameters(parameterNames: List<String>, parameterValues: List<String>): Map<String, String?> {
        return HashMap<String, String?>().apply {
            parameterNames.forEachIndexed { index, name ->
                if (parameterValues[index].isValuePresent()) put(name, parameterValues[index]) else put(name, null)
            }
        }
    }

    private fun String.isNullValue() = equals(TestArgumentTableTokens.nullMarker)
    private fun String.isValuePresent() = !isNullValue()

    private fun SimpleJdbcCall.safeExecute(parameters: Map<String, String?>): Map<String, Any?> {
        val result: Map<String, Any?>
        try {
            result = this.execute(parameters)
        } catch (e: DataAccessException) {
            result = mapOf(Pair(TestArgumentTableTokens.errorColumnName, e.cause?.message ?: e.message))
        }
        return result
    }

}
