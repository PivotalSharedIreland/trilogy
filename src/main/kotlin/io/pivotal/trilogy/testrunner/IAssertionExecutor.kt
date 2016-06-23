package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TrilogyAssertion

interface IAssertionExecutor {
    infix fun execute(assertion: TrilogyAssertion): Boolean
}
