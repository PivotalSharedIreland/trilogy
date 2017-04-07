package io.pivotal.trilogy.application

import io.pivotal.trilogy.i18n.MessageCreator.getI18nMessage
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
                if (testResults.didFail or testResults.malformedTestCases.isNotEmpty()) {
                    suppressStacktrace = true
                    throw ApplicationRunFailed()
                }
            } catch (e: RuntimeException) {
                if (!suppressStacktrace) printFailure(e)
                throw ApplicationRunFailed()
            }
        } else {
            printFailure(null)
            throw ApplicationRunFailed()
        }
    }

    private fun printFailure(e: Throwable?) {
        println("$e")
        e?.stackTrace?.forEach(::println)
        System.out.println(getI18nMessage("applicationUsage"))
    }
}

