package io.pivotal.trilogy.application

import io.pivotal.trilogy.parsing.TrilogyApplicationOptionsParser
import io.pivotal.trilogy.reporting.TestCaseReporter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
open class TrilogyApplicationRunner : ApplicationRunner {

    @Autowired
    lateinit var trilogyController: TrilogyController

    override fun run(args: ApplicationArguments?) {
        var suppressStacktrace = false
        if (args != null) {
            try {
                val applicationOptions = TrilogyApplicationOptionsParser.parse(args.sourceArgs)
                val testResults = trilogyController.run(applicationOptions)
                val output = TestCaseReporter.generateReport(testResults)
                System.out.println(output)
                if (testResults.didFail) {
                    suppressStacktrace = true
                    throw RuntimeException()
                }
            } catch (e: RuntimeException) {
                if (!suppressStacktrace) printFailure(e.stackTrace)
                throw e
            }
        } else {
            printFailure(null)
        }
    }

    private fun printFailure(stackTrace: Array<StackTraceElement>?) {
        stackTrace?.forEach { frame -> System.out.println(frame) }
        System.out.println("Usage: trilogy [<filePath>|--project=<path to trilogy test project>] --db-url=<jdbc url>")
    }
}

