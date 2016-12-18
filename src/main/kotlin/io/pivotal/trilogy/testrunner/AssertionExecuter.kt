package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TrilogyAssertion

interface AssertionExecuter {
    infix fun executeReturningFailureMessage(assertion: TrilogyAssertion): String?
}
