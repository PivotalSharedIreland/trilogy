package io.pivotal.trilogy.application

import io.pivotal.trilogy.testproject.TestProjectBuilder
import io.pivotal.trilogy.testproject.TestProjectResult
import io.pivotal.trilogy.testproject.TrilogyRunResult
import io.pivotal.trilogy.testrunner.UnrecoverableException
import io.pivotal.trilogy.testrunner.TestProjectRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
open class TrilogyController {

    @Autowired
    lateinit var testProjectRunner: TestProjectRunner

    fun run(options: TrilogyApplicationOptions): TrilogyRunResult {
        val testProject = TestProjectBuilder.build(options)
        try {
            return TrilogyRunResult(testProjectRunner.run(testProject), testProject.malformedTestCases)
        } catch(e: UnrecoverableException) {
            return TrilogyRunResult(TestProjectResult(emptyList(), e.localizedMessage, fatalFailure = true), testProject.malformedTestCases)
        }

    }
}

