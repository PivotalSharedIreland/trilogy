package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.i18n.MessageCreator.getI18nMessage
import io.pivotal.trilogy.parsing.exceptions.InvalidTestFormat
import io.pivotal.trilogy.parsing.exceptions.MissingAssertionBody
import io.pivotal.trilogy.parsing.exceptions.MissingAssertionDescription
import io.pivotal.trilogy.testcase.TrilogyAssertion

abstract class BaseStringTestParser(val testBody: String) : TestParser {

    protected val testHeaderRegex = Regex("\\A\\s*## TEST\\s*")
    protected val assertionHeaderRegex = Regex("### ASSERTIONS\\s+(.*)", RegexOption.DOT_MATCHES_ALL)
    abstract val description: String?

    open protected fun validate() {
        if (!testBody.hasValidFormat()) throw InvalidTestFormat("Unable to recognise the test")
    }

    protected fun String.hasValidFormat() : Boolean = this.contains(testHeaderRegex)

    protected fun parseAssertions(): List<TrilogyAssertion> {
        val assertionsSection = assertionHeaderRegex.find(testBody)?.groups?.get(1)?.value ?: return emptyList()

        return Regex("#### SQL\\s*\\n").split(assertionsSection).filter { it.trim().isNotEmpty() }.map { parseAssertion(it) }
    }

    private fun parseAssertion(assertionString: String): TrilogyAssertion {
        val matches = Regex("```").split(assertionString).map(String::trim)
        if (matches[0].isNullOrBlank())
            throw MissingAssertionDescription(getI18nMessage("testParser.assertions.errors.missingDescription.message"), description!!.trim())
        if ((matches.count() < 2) || (matches[1].isNullOrBlank()))
            throw MissingAssertionBody(getI18nMessage("testParser.assertions.errors.missingBody.message"), description!!.trim())
        return TrilogyAssertion(matches[0], matches[1])
    }
}