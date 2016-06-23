package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testrunner.IAssertionExecutor

class AssertionExecutorStub : IAssertionExecutor {
    var passAllExecutedAssertions: Boolean = false

    override fun execute(assertion: TrilogyAssertion): Boolean {
        return passAllExecutedAssertions
    }
}
