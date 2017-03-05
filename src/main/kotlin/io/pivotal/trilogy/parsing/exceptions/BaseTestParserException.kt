package io.pivotal.trilogy.parsing.exceptions

open class BaseTestParserException(message: String?, val testName: String) : StringParserException(message)