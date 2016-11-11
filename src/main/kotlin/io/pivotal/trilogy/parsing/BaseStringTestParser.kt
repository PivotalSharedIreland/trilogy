package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.TrilogyAssertion

abstract class BaseStringTestParser(val testBody: String) : TestParser {
    class InvalidTestFormat(message: String?) : RuntimeException(message)
    class MissingDescription(message: String?) : RuntimeException(message)

    protected val testHeaderRegex = Regex("\\A\\s*## TEST\\s*")
    protected val assertionHeaderRegex = Regex("### ASSERTIONS\\s+(.*)", RegexOption.DOT_MATCHES_ALL)

    open protected fun validate() {
        if (!testBody.hasValidFormat()) throw InvalidTestFormat("Unable to recognise the test")
    }

    protected fun String.hasValidFormat() : Boolean = this.contains(testHeaderRegex)

    protected fun parseAssertions(): List<TrilogyAssertion> {
        val assertionsSection = assertionHeaderRegex.find(testBody)?.groups?.get(1)?.value ?: return emptyList()

        return Regex("#### SQL\\s*\\n(.+?)```(.+?)```", RegexOption.DOT_MATCHES_ALL)
                .findAll(assertionsSection)
                .map { match -> TrilogyAssertion(match.groupValues[1].trim(), match.groupValues[2].trim()) }
                .toList()
    }
}