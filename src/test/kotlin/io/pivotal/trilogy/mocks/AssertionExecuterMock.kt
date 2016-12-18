package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testrunner.AssertionExecuter
import io.pivotal.trilogy.testrunner.ScriptExecuter

class AssertionExecuterMock(val scriptExecuter: ScriptExecuter) : AssertionExecuter {
    var assertionExecutionErrorMessage: String? = null
    var assertions = mutableListOf<TrilogyAssertion>()

    override fun executeReturningFailureMessage(assertion: TrilogyAssertion): String? {
        scriptExecuter.execute(assertion.body)
        return assertionExecutionErrorMessage
    }
}
