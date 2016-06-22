package io.pivotal.trilogy.testrunner

import org.springframework.jdbc.core.simple.SimpleJdbcCall
import java.util.*

class TestSubjectCaller (val subject: SimpleJdbcCall) {

    fun call(procedureName: String, parameterNames: List<String>, parameterValues: List<String>): Map<String, Any?> {
        subject.withProcedureName(procedureName) //FIXME (stateful dependency, should use factory or something)
        return subject.execute(inputParameters(parameterNames, parameterValues))
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
