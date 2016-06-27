package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testrunner.ScriptExecuter

class ScriptExecuterSpy : ScriptExecuter {
    var executeCalls = 0
    val executeArgList = mutableListOf<String>()

    override fun execute(scriptBody: String) {
        executeCalls ++
        executeArgList.add(scriptBody)
    }
}