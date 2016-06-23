package io.pivotal.trilogy.testrunner

interface ITestSubjectCaller {
    fun call(procedureName: String, parameterNames: List<String>, parameterValues: List<String>): Map<String, Any?>
}
