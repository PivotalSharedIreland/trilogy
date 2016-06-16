package io.pivotal.trilogy.application

import io.pivotal.trilogy.parsing.TrilogyApplicationOptionsParser


public fun main(arguments: Array<String>) {
    val applicationOptions = TrilogyApplicationOptionsParser.parse(arguments)
    val output = if (TrilogyApplication().run(applicationOptions)) "All tests passed" else "Some tests failed"
    System.out.println(output)
}
