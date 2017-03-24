package io.pivotal.trilogy.parsing.exceptions.test

import io.pivotal.trilogy.parsing.exceptions.StringParserException

open class BaseParseException(message: String?, val testName: String) : StringParserException(message)