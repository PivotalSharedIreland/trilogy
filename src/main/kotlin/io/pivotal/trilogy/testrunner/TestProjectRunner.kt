package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testproject.TestProjectResult
import io.pivotal.trilogy.testproject.TrilogyTestProject

interface TestProjectRunner {
    fun run(project: TrilogyTestProject): TestProjectResult
}