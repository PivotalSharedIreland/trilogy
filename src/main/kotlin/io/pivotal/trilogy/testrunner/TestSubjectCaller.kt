package io.pivotal.trilogy.testrunner

import org.springframework.jdbc.core.simple.SimpleJdbcCall
import java.util.HashMap


class TestSubjectCaller(val subject: SimpleJdbcCall, val functionName: String, val parameterNames: List<String>) {
    init {
        subject.withProcedureName(functionName)
    }

    fun call(values: List<String>): Map<String, Any> {
        return subject.execute(inputParameters(values))
    }

    private fun inputParameters(values: List<String>): Map<String, String> {
        return HashMap<String, String>().apply {
            parameterNames.forEachIndexed { index, name ->
                put(name, values.get(index))
            }
        }
    }

}