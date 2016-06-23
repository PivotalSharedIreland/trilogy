package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TrilogyAssertion

interface AssertionExecutor {
    infix fun execute(assertion: TrilogyAssertion): Boolean
}
