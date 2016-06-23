package io.pivotal.trilogy.testrunner

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import java.util.*
import javax.sql.DataSource

class DatabaseTestSubjectCaller(@Autowired val dataSource: DataSource) : TestSubjectCaller {

    override fun call(procedureName: String, parameterNames: List<String>, parameterValues: List<String>): Map<String, Any?> {
        return jdbcCall(procedureName).execute(inputParameters(parameterNames, parameterValues))
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

    private fun String.isNullValue() = equals("__NULL__")
    private fun String.isValuePresent() = !isNullValue()

}
