package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testrunner.ScriptExecuter

class ScriptExecuterSpy : ScriptExecuter {
    var executeCalls = 0
    val executeArgList = mutableListOf<String>()

    override fun execute(script: String) {
        executeCalls ++
        executeArgList.add(script)
    }
}