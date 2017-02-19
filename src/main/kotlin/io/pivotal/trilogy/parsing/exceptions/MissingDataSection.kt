package io.pivotal.trilogy.parsing.exceptions

class MissingDataSection(message: String?, val testName: String) : ProceduralTestParseException(message)