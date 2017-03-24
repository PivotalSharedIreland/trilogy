package io.pivotal.trilogy.parsing.exceptions.testcase

import io.pivotal.trilogy.parsing.exceptions.StringParserException

class TestCaseParseException(message: String, testCasePath: String) : StringParserException(message)