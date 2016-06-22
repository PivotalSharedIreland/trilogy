package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import java.net.URL

interface TestProjectRunner {
    open fun run(projectUrl: URL): TestCaseResult
}