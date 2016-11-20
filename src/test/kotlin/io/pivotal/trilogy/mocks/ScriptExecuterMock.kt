package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testrunner.ScriptExecuter

class ScriptExecuterMock : ScriptExecuter {
    val executeCalls: Int get() = executeArgList.count()
    val executeArgList = mutableListOf<String>()
    var shouldFailExecution = false

    override fun execute(scriptBody: String) {
        if (shouldFailExecution) {
            throw RuntimeException("SQL Script exception")
        }
        executeArgList.add(scriptBody)
    }
}