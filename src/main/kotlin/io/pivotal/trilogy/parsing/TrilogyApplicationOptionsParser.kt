package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.application.TrilogyApplicationOptions
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException

object TrilogyApplicationOptionsParser {

    fun parse(arguments: Array<String>): TrilogyApplicationOptions {
        val options = Options().apply {
            addOption("", "project", true, "Path to the test project directory")
            addOption("", "db-url", true, "Database URL")
        }
        val command: CommandLine
        try {
            command = DefaultParser().parse(options, arguments)
        } catch (e: ParseException) {
            System.out.println(e.message)
            throw e
        }

        val applicationOptions = if (command.args.any())
            TrilogyApplicationOptions(testCaseFilePath = command.args.first())
        else
            TrilogyApplicationOptions(testProjectPath = command.getOptionValue("project") ?: "")

        return applicationOptions
    }
}