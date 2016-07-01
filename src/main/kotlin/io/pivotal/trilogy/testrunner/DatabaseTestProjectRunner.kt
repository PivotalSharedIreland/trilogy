package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.parsing.StringTestCaseParser
import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testproject.FixtureLibrary
import io.pivotal.trilogy.testproject.TestProjectResources
import java.io.File
import java.net.URL

class DatabaseTestProjectRunner(val testCaseRunner: TestCaseRunner, val scriptExecuter: ScriptExecuter) : TestProjectRunner {
    inner class TestProjectExecutor(val projectUrl: URL) {
        val resources = TestProjectResources(projectUrl)

        fun run(): TestCaseResult {
            if (resources.testsAbsent) return TestCaseResult()
            applySchema()
            runSourceScripts()
            return runTestCases()
        }

        private fun applySchema() {
            if (resources.schemaFile.isFile) executeSqlFile(resources.schemaFile)
        }

        private fun runSourceScripts() {
            resources.sourceDirectory.apply {
                isDirectory && listFiles().filter { file -> file.name.endsWith(".sql") }
                        .map { file -> executeSqlFile(file) }.any()
            }
        }

        private fun executeSqlFile(file: File) {
            scriptExecuter.execute(file.readText())
        }

        private fun runTestCases(): TestCaseResult {
            val testCaseResults = resources.testsDirectory.listFiles()
                    .filter { file -> file.name.endsWith(".stt") }
                    .map { testFile ->
                        testCaseRunner.run(StringTestCaseParser(testFile.readText()).getTestCase(), FixtureLibrary.emptyLibrary())
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