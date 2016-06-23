package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.parsing.StringTestCaseParser
import io.pivotal.trilogy.reporting.TestCaseResult
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.net.URL

class DatabaseTestProjectRunner(val testCaseRunner: TestCaseRunner, val scriptExecuter: ScriptExecuter) : TestProjectRunner {

    override fun run(projectUrl: URL): TestCaseResult {
        if (testsAbsentAtUrl(projectUrl)) return TestCaseResult()
        applySchema(projectUrl)
        runSourceScripts(projectUrl)
        return runTestCases(projectUrl)
    }

    private fun applySchema(projectUrl: URL) {
        val schema = schemaFile(projectUrl)
        if (schema.isFile) scriptExecuter.execute(schema.readText())
    }

    private fun runSourceScripts(projectUrl: URL) {
        sourceDirectory(projectUrl).apply {
            isDirectory && listFiles().filter { file -> file.name.endsWith(".sql") }
                    .map { file ->
                scriptExecuter.execute(file.readText())
            }.any()
        }
    }

    private fun runTestCases(projectUrl: URL): TestCaseResult {
        val testCaseResults = testsDirectory(projectUrl).listFiles()
                .filter { file -> file.name.endsWith(".stt") }
                .map { testFile ->
            testCaseRunner.run(StringTestCaseParser(testFile.readText()).getTestCase())
        }

        return testCaseResults.fold(TestCaseResult()) { accumulated, current ->
            accumulated + current
        }
    }

    private fun testsDirectory(projectUrl: URL) = File("${projectUrl.path}tests")
    private fun sourceDirectory(projectUrl: URL) = File("${projectUrl.path}src")
    private fun schemaFile(projectUrl: URL) = File("${projectUrl.path}tests/fixtures/schema.sql")
    private fun testsAbsentAtUrl(projectUrl: URL): Boolean = !testsPresentAtUrl(projectUrl)

    private fun testsPresentAtUrl(projectUrl: URL): Boolean {
        val testsDirectory = testsDirectory(projectUrl)
        return testsDirectory.isDirectory && testsDirectory.listFiles().any { file -> file.name.endsWith(".stt") }
    }
}