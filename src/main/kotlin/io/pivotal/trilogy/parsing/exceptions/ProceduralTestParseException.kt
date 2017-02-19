package io.pivotal.trilogy.parsing.exceptions

open class ProceduralTestParseException(message: String?, val testName: String) : StringParserException(message)