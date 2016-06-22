package io.pivotal.trilogy.application

import io.pivotal.trilogy.parsing.TrilogyApplicationOptionsParser
import io.pivotal.trilogy.reporting.TestCaseReporter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class TrilogyApplicationRunner : ApplicationRunner {

    @Autowired
    lateinit var trilogyController: TrilogyController

    override fun run(args: ApplicationArguments?) {
        if (args != null && args.sourceArgs.size > 0) {
            try {
                val applicationOptions = TrilogyApplicationOptionsParser.parse(args.sourceArgs)
                val output = TestCaseReporter.generateReport(trilogyController.run(applicationOptions))
                System.out.println(output)
            } catch (e: RuntimeException) {
                printFailure()
            }
        } else {
            printFailure()
        }
    }

    private fun printFailure() {
        System.out.println("Invalid command (filepath required). Usage: trilogy <filePath>")
    }

}

