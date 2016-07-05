package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testproject.TrilogyTestProject

class DatabaseTestProjectRunner(val testCaseRunner: TestCaseRunner, val scriptExecuter: ScriptExecuter) : TestProjectRunner {

    override fun run(project: TrilogyTestProject): TestCaseResult {
        return project.runTests()
    }

    private fun TrilogyTestProject.runTests(): TestCaseResult {
        applySchema()
        runSourceScripts()
        return runTestCases()
    }

    private fun TrilogyTestProject.runSourceScripts() {
        sourceScripts.map { script -> scriptExecuter.execute(script) }
    }

    private fun TrilogyTestProject.runTestCases(): TestCaseResult {
        return this.testCases.fold(TestCaseResult()) { result, testCase ->
            result + testCaseRunner.run(testCase, this.fixtures)
        }
    }

    private fun TrilogyTestProject.applySchema() {
        schema?.let { scriptExecuter.execute(it) }
    }
}

