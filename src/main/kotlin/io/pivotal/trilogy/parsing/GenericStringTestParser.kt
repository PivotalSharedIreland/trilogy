package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.i18n.MessageCreator.getI18nMessage
import io.pivotal.trilogy.parsing.exceptions.MissingTestDescription
import io.pivotal.trilogy.parsing.exceptions.MissingTestBody
import io.pivotal.trilogy.testcase.GenericTrilogyTest

class GenericStringTestParser(testBody: String) : BaseStringTestParser(testBody) {

    private val headerlessBody: String by lazy { testBody.replace(testHeaderRegex, "") }

    private val testSection: String by lazy {
        headerlessBody.replace(Regex("\\s*#+ [A-Z]{4,}.*", RegexOption.DOT_MATCHES_ALL), "").trim()
    }

    private val test: String? by lazy {
        Regex("```(.+?)```", RegexOption.DOT_MATCHES_ALL).find(testSection)?.groupValues.orEmpty().getOrNull(1)
    }

    private val description: String? by lazy {
        Regex("(.+?)```", RegexOption.DOT_MATCHES_ALL).find(headerlessBody)?.groupValues.orEmpty().getOrElse(1, { _ -> testSection })
    }

    init {
        validate()
    }

    override fun getTest(): GenericTrilogyTest {
        return GenericTrilogyTest(description!!.trim(), test!!.trim(), parseAssertions())
    }

    override fun validate() {
        super.validate()
        if (description == null || description!!.contains(Regex("\\A\\s*```")))
            throw MissingTestDescription(
                    getI18nMessage("testParser.generic.errors.missingDescription.message"),
                    getI18nMessage("testParser.generic.errors.missingDescription.testName")
            )
        if (test == null) throw MissingTestBody(getI18nMessage("testParser.generic.errors.missingBody.message"), description!!)
    }

}