package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.TestCaseParser

abstract class BaseStringTestCaseParser(val testCaseBody: String) : TestCaseParser {
    class InvalidTestCaseFormat(message: String?) : RuntimeException(message)
    class MissingDescription(message: String?) : RuntimeException(message)

    open protected val testCaseHeaderRegex = Regex("^# TEST CASE")
    protected val testStrings: List<String> by lazy {
        testCaseBody.split("## TEST").drop(1).map { "## TEST$it".trim() }
    }

    private val hookSectionNameSplitRegex = Regex("^-\\s+", RegexOption.MULTILINE)
    private val dotMatchesAllAndMultiline = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
    private val descriptionSplitRegex = Regex("^\\s*##.*", dotMatchesAllAndMultiline)

    private val parsedDescription: String by lazy {
        testCaseBody.replace(testCaseHeaderRegex, "").replace(descriptionSplitRegex, "").trim()
    }

    protected abstract fun validate()

    protected fun String.hasValidTest() = this.contains(Regex("## TEST"))

    protected fun parseHookSection(sectionName: String): List<String> {
        val matchResult = hookSectionHeaderRegex(sectionName).find(testCaseBody)
        val list = matchResult?.groupValues?.get(1)?.let { nameListString ->
            nameListString.trim().split(hookSectionNameSplitRegex)
                    .filter(String::isNotBlank).map(String::trim).toList()
        }
        return list ?: emptyList()
    }

    private fun hookSectionHeaderRegex(sectionName: String): Regex {
        return Regex("^## $sectionName\\s(.*?)##", dotMatchesAllAndMultiline)
    }


    protected fun parseDescription(): String {
        if (parsedDescription.isEmpty()) throw MissingDescription("Every test case must have a description")
        return parsedDescription
    }

}