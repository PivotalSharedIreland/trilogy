package io.pivotal.trilogy.application

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testproject.TestProjectBuilder
import io.pivotal.trilogy.testrunner.TestProjectRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
open class TrilogyController {

    @Autowired
    lateinit var testProjectRunner: TestProjectRunner

    fun run(options: TrilogyApplicationOptions): List<TestCaseResult> {
        val testProject = TestProjectBuilder.build(options)
        return testProjectRunner.run(testProject)
    }
}
