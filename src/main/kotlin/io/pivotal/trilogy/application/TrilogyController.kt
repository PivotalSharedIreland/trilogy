package io.pivotal.trilogy.application

import io.pivotal.trilogy.testproject.TestProjectBuilder
import io.pivotal.trilogy.testproject.TestProjectResult
import io.pivotal.trilogy.testrunner.InconsistentDatabaseException
import io.pivotal.trilogy.testrunner.TestProjectRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
open class TrilogyController {

    @Autowired
    lateinit var testProjectRunner: TestProjectRunner

    fun run(options: TrilogyApplicationOptions): TestProjectResult {
        val testProject = TestProjectBuilder.build(options)
        try {
            return testProjectRunner.run(testProject)
        } catch(e: InconsistentDatabaseException) {
            return TestProjectResult(emptyList(), e.localizedMessage)
        }

    }
}

