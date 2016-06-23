package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testrunner.AssertionExecutor

class AssertionExecutorStub : AssertionExecutor {
    var passAllExecutedAssertions: Boolean = false

    override fun execute(assertion: TrilogyAssertion): Boolean {
        return passAllExecutedAssertions
    }
}
