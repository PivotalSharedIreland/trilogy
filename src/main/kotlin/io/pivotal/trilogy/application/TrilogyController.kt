package io.pivotal.trilogy.application

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testproject.TestProjectBuilder
import io.pivotal.trilogy.testproject.TestProjectResourceLocator
import io.pivotal.trilogy.testproject.UrlTestCaseResourceLocator
import io.pivotal.trilogy.testproject.UrlTestProjectResourceLocator
import io.pivotal.trilogy.testrunner.TestProjectRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import java.io.File

@Controller
open class TrilogyController {

    @Autowired
    lateinit var testProjectRunner: TestProjectRunner

    fun run(options: TrilogyApplicationOptions): TestCaseResult {
        val testProject = TestProjectBuilder.build(options.getResourceLocator())
        return testProjectRunner.run(testProject)
    }

    private fun TrilogyApplicationOptions.getResourceLocator(): TestProjectResourceLocator {
        return if (testCaseFilePath.isNullOrEmpty())
            UrlTestProjectResourceLocator(File(testProjectPath).toURI().toURL())
        else
            UrlTestCaseResourceLocator(File(testCaseFilePath).toURI().toURL())
    }
}
