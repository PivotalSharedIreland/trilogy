package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.StringTestCaseParser
import io.pivotal.trilogy.testproject.FixtureLibrary
import io.pivotal.trilogy.testproject.UrlTestProjectResourceLocator
import java.net.URL

class DatabaseTestProjectRunner(val testCaseRunner: TestCaseRunner, val scriptExecuter: ScriptExecuter) : TestProjectRunner {
    inner class TestProjectExecutor(projectUrl: URL) {
        val resources = UrlTestProjectResourceLocator(projectUrl)

        fun run(): TestCaseResult {
            if (resources.testsAbsent) return TestCaseResult()
            applySchema()
            runSourceScripts()
            return runTestCases()
        }

        private fun applySchema() {
            if (resources.schema != null) scriptExecuter.execute(resources.schema!!)
        }

        private fun runSourceScripts() {
            resources.sourceScripts.map { script -> scriptExecuter.execute(script) }
        }

        private fun runTestCases(): TestCaseResult {
            val testCaseResults = resources.testCases.map { testCase ->
                testCaseRunner.run(StringTestCaseParser(testCase).getTestCase(), FixtureLibrary.emptyLibrary())
            }

            return testCaseResults.fold(TestCaseResult()) { accumulated, current ->
                accumulated + current
            }
        }

    }

    override fun run(projectUrl: URL): TestCaseResult {
        return TestProjectExecutor(projectUrl).run()
    }

}