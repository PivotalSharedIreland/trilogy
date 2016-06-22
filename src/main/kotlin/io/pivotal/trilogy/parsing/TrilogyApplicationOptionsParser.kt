package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.application.TrilogyApplicationOptions
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options

class TrilogyApplicationOptionsParser {
    companion object {

        fun parse(arguments: Array<String>): TrilogyApplicationOptions {
            val command = DefaultParser().parse(Options(), arguments)
            return TrilogyApplicationOptions(command.args.first())
        }
    }
}
