package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testproject.TrilogyTestProject

class DatabaseTestProjectRunner(val testCaseRunner: TestCaseRunner, val scriptExecuter: ScriptExecuter) : TestProjectRunner {

    override fun run(project: TrilogyTestProject): List<TestCaseResult> {
        return project.runTests()
    }

    private fun TrilogyTestProject.runTests(): List<TestCaseResult> {
        applySchema()
        runSourceScripts()
        return runTestCases()
    }

    private fun TrilogyTestProject.runSourceScripts() {
        sourceScripts.forEach { script -> scriptExecuter.execute(script) }
    }

    private fun TrilogyTestProject.runTestCases(): List<TestCaseResult> {
        return this.testCases.map { testCaseRunner.run(it, this.fixtures) }
    }

    private fun TrilogyTestProject.applySchema() {
        schema?.let { scriptExecuter.execute(it) }
    }
}

