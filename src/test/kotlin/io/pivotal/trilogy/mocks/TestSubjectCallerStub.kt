package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testrunner.TestSubjectCaller

class TestSubjectCallerStub : TestSubjectCaller {
    var resultToReturn: Map<String, String> = emptyMap()
    var exceptionToThrow: RuntimeException? = null

    override fun call(procedureName: String, parameterNames: List<String>, parameterValues: List<String>): Map<String, Any?> {
        handleExceptionToThrow()
        return resultToReturn
    }

    private fun handleExceptionToThrow() {
        val exception = exceptionToThrow
        if (exception != null) throw exception
    }

}
