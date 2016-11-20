package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testrunner.AssertionExecuter
import io.pivotal.trilogy.testrunner.ScriptExecuter

class AssertionExecuterMock(val scriptExecuter: ScriptExecuter) : AssertionExecuter {
    var passAllExecutedAssertions = false
    var assertions = mutableListOf<TrilogyAssertion>()

    override fun execute(assertion: TrilogyAssertion): Boolean {
        scriptExecuter.execute(assertion.body)
        return passAllExecutedAssertions
    }
}
