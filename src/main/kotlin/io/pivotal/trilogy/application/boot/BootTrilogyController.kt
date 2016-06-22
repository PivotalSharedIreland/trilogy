package io.pivotal.trilogy.application.boot

import io.pivotal.trilogy.application.TrilogyApplicationOptions
import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.UrlTestCaseReader
import io.pivotal.trilogy.testrunner.AssertionExecutor
import io.pivotal.trilogy.testrunner.TestCaseRunner
import io.pivotal.trilogy.testrunner.TestSubjectCaller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import java.io.File

@Controller
open class BootTrilogyController {

    @Autowired
    lateinit var testSubjectCaller: TestSubjectCaller
    @Autowired
    lateinit var assertionExecutor: AssertionExecutor

    fun run(options: TrilogyApplicationOptions): TestCaseResult {
        val testCaseUrl = File(options.testCaseFilePath).toURI().toURL()
        val trilogyTestCase = UrlTestCaseReader(testCaseUrl).getTestCase()
        return TestCaseRunner(testSubjectCaller,assertionExecutor).run(trilogyTestCase)
    }

}
