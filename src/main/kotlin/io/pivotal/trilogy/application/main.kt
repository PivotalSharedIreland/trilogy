package io.pivotal.trilogy.application

import io.pivotal.trilogy.parsing.TrilogyApplicationOptionsParser
import io.pivotal.trilogy.reporting.TestCaseReporter


fun main(arguments: Array<String>) {
    val applicationOptions = TrilogyApplicationOptionsParser.parse(arguments)
    val output = TestCaseReporter.generateReport(TrilogyApplication().run(applicationOptions))
    System.out.println(output)
}
