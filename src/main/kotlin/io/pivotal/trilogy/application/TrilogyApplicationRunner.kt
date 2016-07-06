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
        if (args != null) {
            try {
                val applicationOptions = TrilogyApplicationOptionsParser.parse(args.sourceArgs)
                printBanner()
                val output = TestCaseReporter.generateReport(trilogyController.run(applicationOptions))
                System.out.println(output)
            } catch (e: RuntimeException) {
                printFailure(e.stackTrace)
            }
        } else {
            printFailure(null)
        }
    }

    private fun printFailure(stackTrace: Array<StackTraceElement>?) {
        stackTrace?.forEach { frame -> System.out.println(frame) }
        System.out.println("Usage: trilogy [<filePath>|--project=<path to trilogy test project>] --db-url=<jdbc url>")
    }

    private fun printBanner() {
        val banner = TrilogyApplicationRunner::class.java
                .getResourceAsStream("/banner.txt").reader().readText()
        System.out.println(banner)
    }

}

