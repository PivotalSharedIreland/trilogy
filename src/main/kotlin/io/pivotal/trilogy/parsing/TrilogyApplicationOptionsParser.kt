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
            addOption("", "skip_schema_load", false, "Use this flag to skip loading schema from the project")
        }
        val command: CommandLine
        try {
            command = DefaultParser().parse(options, arguments.filter { it.isValidOption }.toTypedArray())
        } catch (e: ParseException) {
            System.out.println(e.message)
            throw e
        }

        val applicationOptions = if (command.args.any())
            TrilogyApplicationOptions(testCaseFilePath = command.args.first())
        else {
            val testProjectPath = command.getOptionValue("project") ?: ""
            val shouldSkipSchema = command.hasOption("skip_schema_load")
            TrilogyApplicationOptions(testProjectPath = testProjectPath, shouldSkipSchema = shouldSkipSchema)
        }

        return applicationOptions
    }

    private val String.isValidOption: Boolean get() {
        val validOptions = listOf("--project=", "--skip_schema_load")
        return this.startsWith("--").not() || validOptions.any { this.startsWith(it) }
    }

}