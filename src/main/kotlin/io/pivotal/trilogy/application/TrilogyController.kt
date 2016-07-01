package io.pivotal.trilogy.application

import io.pivotal.trilogy.parsing.UrlTestCaseParser
import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testproject.FixtureLibrary
import io.pivotal.trilogy.testrunner.TestCaseRunner
import io.pivotal.trilogy.testrunner.TestProjectRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import java.io.File

@Controller
open class TrilogyController {

    @Autowired
    lateinit var testCaseRunner: TestCaseRunner

    @Autowired
    lateinit var testProjectRunner: TestProjectRunner

    fun run(options: TrilogyApplicationOptions): TestCaseResult {
        return if (options.testCaseFilePath.isNullOrEmpty()) runTestProject(options) else runTestCase(options)
    }

    private fun runTestProject(options: TrilogyApplicationOptions): TestCaseResult {
        val projectUrl = File(options.testProjectPath).toURI().toURL()
        return testProjectRunner.run(projectUrl)
    }

    private fun runTestCase(options: TrilogyApplicationOptions): TestCaseResult {
        val testCaseUrl = File(options.testCaseFilePath).toURI().toURL()
        val trilogyTestCase = UrlTestCaseParser(testCaseUrl).getTestCase()
        return testCaseRunner.run(trilogyTestCase, FixtureLibrary.emptyLibrary())
    }
}
