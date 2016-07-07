package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testrunner.ScriptExecuter

class ScriptExecuterSpy : ScriptExecuter {
    val executeCalls: Int get() = executeArgList.count()
    val executeArgList = mutableListOf<String>()

    override fun execute(scriptBody: String) {
        executeArgList.add(scriptBody)
    }
}