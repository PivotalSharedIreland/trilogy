package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testrunner.TestCaseRunner

class TestCaseRunnerSpy : TestCaseRunner {
    var runCount = 0
    var runResult = TestCaseResult(0, 0)
    var runArgument: TrilogyTestCase? = null

    override fun run(trilogyTestCase: TrilogyTestCase): TestCaseResult {
        runCount++
        runArgument = trilogyTestCase
        return runResult
    }
}