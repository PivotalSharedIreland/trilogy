package io.pivotal.trilogy.testcase

import io.pivotal.trilogy.parsing.StringTestParser

class StringTestCaseParser(val testCaseBody: String) : TestCaseParser {

    class InvalidTestCaseFormat(message: String?) : RuntimeException(message) {}
    class MissingDescription(message: String?) : RuntimeException(message) {}
    class MissingFunctionName(message: String?) : RuntimeException(message) {}

    private val testCaseHeaderRegex = Regex("^# TEST CASE (\\S*)\\s+")
    private val hookSectionNameSplitRegex = Regex("^-\\s+", RegexOption.MULTILINE)

    init {
        validate()
    }

    private fun validate() {
        if (!testCaseBody.isValidTestCase()) throw InvalidTestCaseFormat("Unable to recognise a test case")
    }

    override fun getTestCase(): TrilogyTestCase {
        return parse()
    }

    private fun parse(): TrilogyTestCase {
        val tests = parseTests().map { it as? ProcedureTrilogyTest }.filterNotNull()
        return ProcedureTrilogyTestCase(parseFunctionName(), parseDescription(), tests, parseHooks())
    }

    private fun parseDescription(): String {
        val description = testCaseBody.replace(testCaseHeaderRegex, "").replace(Regex("\\s*## TEST.*", RegexOption.DOT_MATCHES_ALL), "").trim()
        if (description.isEmpty()) throw MissingDescription("Every test case must have a description")
        return description
    }

    private fun parseFunctionName(): String {
        val functionName = testCaseHeaderRegex.find(testCaseBody)!!.groupValues[1].trim()
        if (functionName.isEmpty()) throw MissingFunctionName("A test case should specify a function name for testing")
        return functionName
    }

    private fun parseTests(): List<TrilogyTest> {
        val individualTestSections = testCaseBody.split("## TEST").drop(1).map { "## TEST$it".trim() }
        return individualTestSections.map { StringTestParser(it).getTest() }
    }

    private fun parseHooks(): TestCaseHooks {
        return TestCaseHooks(
                beforeAll = parseHookSection("BEFORE ALL"),
                beforeEachTest = parseHookSection("BEFORE EACH TEST"),
                afterAll = parseHookSection("AFTER ALL"),
                afterEachTest = parseHookSection("AFTER EACH TEST"),
                beforeEachRow = parseHookSection("BEFORE EACH ROW"),
                afterEachRow = parseHookSection("AFTER EACH ROW")
        )
    }


    private fun parseHookSection(sectionName: String): List<String> {
        val matchResult = hookSectionHeaderRegex(sectionName).find(testCaseBody)
        val list = matchResult?.groupValues?.get(1)?.let { nameListString ->
            nameListString.trim().split(hookSectionNameSplitRegex)
                    .filter { name -> name.isNotBlank() }.map { name -> name.trim() }.toList()
        }
        return list ?: emptyList()
    }

    private fun hookSectionHeaderRegex(sectionName: String): Regex {
        return Regex("^## $sectionName\\s(.*?)##", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE))
    }

    private fun String.isValidTestCase(): Boolean {
        return hasValidHeader() && hasValidTest()
    }

    private fun String.hasValidHeader() = this.contains(testCaseHeaderRegex)

    private fun String.hasValidTest() = this.contains(Regex("## TEST"))

}