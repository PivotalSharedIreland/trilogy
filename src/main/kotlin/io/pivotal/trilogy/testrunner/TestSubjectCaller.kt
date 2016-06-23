package io.pivotal.trilogy.testrunner

interface TestSubjectCaller {
    fun call(procedureName: String, parameterNames: List<String>, parameterValues: List<String>): Map<String, Any?>
}
