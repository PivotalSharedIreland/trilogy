package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testproject.FixtureLibrary
import io.pivotal.trilogy.testrunner.TestCaseRunner

class TestCaseRunnerMock : TestCaseRunner {
    var runCount = 0
    var runResult = TestCaseResult()
    var runArgument: TrilogyTestCase? = null

    override fun run(trilogyTestCase: TrilogyTestCase, library: FixtureLibrary): TestCaseResult {
        runCount++
        runArgument = trilogyTestCase
        return runResult
    }
}