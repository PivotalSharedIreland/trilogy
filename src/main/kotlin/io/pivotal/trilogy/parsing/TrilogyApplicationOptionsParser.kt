package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.application.TrilogyApplicationOptions
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options

class TrilogyApplicationOptionsParser {
    companion object {

        fun parse(arguments: Array<String>): TrilogyApplicationOptions {
            val options = Options().apply { addOption("", "db-url", true, "The JDBC URL for the database under test") }
            val command = DefaultParser().parse(options, arguments)
            return TrilogyApplicationOptions(command.args.first(), command.getOptionValue("db-url"))
        }
    }
}