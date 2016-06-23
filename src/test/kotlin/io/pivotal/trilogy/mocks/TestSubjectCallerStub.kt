package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testrunner.TestSubjectCaller

class TestSubjectCallerStub : TestSubjectCaller {
    var resultToReturn: Map<String, String> = emptyMap()

    override fun call(procedureName: String, parameterNames: List<String>, parameterValues: List<String>): Map<String, Any?> {
        return resultToReturn
    }

}
