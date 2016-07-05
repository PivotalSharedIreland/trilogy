package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testproject.TrilogyTestProject

interface TestProjectRunner {
    fun run(project: TrilogyTestProject): TestCaseResult
}