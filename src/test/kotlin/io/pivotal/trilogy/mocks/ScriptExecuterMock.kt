package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testrunner.ScriptExecuter

class ScriptExecuterMock : ScriptExecuter {
    val executeCalls: Int get() = executeArgList.count()
    val executeArgList = mutableListOf<String>()
    var shouldFailExecution = false
    var failureException = RuntimeException("SQL Script exception")


    override fun execute(scriptBody: String) {
        if (shouldFailExecution) {
            throw failureException
        }
        executeArgList.add(scriptBody)
    }
}