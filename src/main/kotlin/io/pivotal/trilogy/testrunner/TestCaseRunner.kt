package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TrilogyTestCase

interface TestCaseRunner {
    fun run(trilogyTestCase: TrilogyTestCase): TestCaseResult
}