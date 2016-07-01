package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testproject.FixtureLibrary

interface TestCaseRunner {
    fun run(trilogyTestCase: TrilogyTestCase, library: FixtureLibrary): TestCaseResult
}