package io.pivotal.trilogy.testrunner

import org.springframework.jdbc.core.simple.SimpleJdbcCall
import java.util.*


class TestSubjectCaller(val subject: SimpleJdbcCall, val functionName: String, val parameterNames: List<String>) {
    init {
        subject.withProcedureName(functionName)
    }

    fun call(values: List<String>): Map<String, Any?> {
        return subject.execute(inputParameters(values))
    }

    private fun inputParameters(values: List<String>): Map<String, String?> {
        return HashMap<String, String?>().apply {
            parameterNames.forEachIndexed { index, name ->
                if (values[index].isValuePresent()) put(name, values[index]) else put(name, null)
            }
        }
    }

    private fun String.isNullValue() = equals("__NULL__")
    private fun String.isValuePresent() = !isNullValue()

}