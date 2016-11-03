package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.GenericTrilogyTest

class GenericStringTestParser(testBody: String) : BaseStringTestParser(testBody) {
    class MissingTestBody(message: String?) : RuntimeException(message)

    private val headerlessBody : String by lazy { testBody.replace(testHeaderRegex, "") }

    private val testSection: String by lazy {
        headerlessBody.replace(Regex("\\s*#+ [A-Z]{4,}.*", RegexOption.DOT_MATCHES_ALL), "").trim()
    }

    private val test: String? by lazy {
        Regex("```(.+?)```", RegexOption.DOT_MATCHES_ALL).find(testSection)?.groupValues.orEmpty().getOrNull(1)
    }

    private val description: String? by lazy {
        Regex("(.+?)```", RegexOption.DOT_MATCHES_ALL).find(headerlessBody)?.groupValues.orEmpty().getOrNull(1)
    }

    init {
        validate()
    }

    override fun getTest(): GenericTrilogyTest {
        return GenericTrilogyTest(description!!.trim(), test!!.trim(), parseAssertions())
    }

    override fun validate() {
        super.validate()
        if (test == null) throw MissingTestBody("Test body not provided")
        if (description == null) throw StringTestParser.MissingDescription("Every test should have a description")
    }

}