package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.application.TrilogyApplicationOptions
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options

object TrilogyApplicationOptionsParser {

    fun parse(arguments: Array<String>): TrilogyApplicationOptions {
        val options = Options().apply {
            addOption("", "project", true, "Path to the test project directory")
        }
        val command = DefaultParser().parse(options, arguments)

        val applicationOptions = if (command.args.any())
            TrilogyApplicationOptions(testCaseFilePath = command.args.first())
        else
            TrilogyApplicationOptions(testProjectPath = command.getOptionValue("project") ?: "")

        return applicationOptions
    }
}