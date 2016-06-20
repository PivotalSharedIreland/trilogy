package io.pivotal.trilogy.application

import io.pivotal.trilogy.testcase.UrlTestCaseReader
import io.pivotal.trilogy.testrunner.TestCaseRunner
import java.io.File

class TrilogyApplication {
    fun run(options: TrilogyApplicationOptions): Boolean {
        val testCaseUrl = File(options.testCaseFilePath).toURI().toURL()
        val trilogyTestCase = UrlTestCaseReader(testCaseUrl).getTestCase()
        return TestCaseRunner(options.jdbcUrl).run(trilogyTestCase).didPass
    }
}