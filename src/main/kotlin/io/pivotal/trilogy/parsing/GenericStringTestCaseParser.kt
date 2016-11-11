package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.GenericTrilogyTest
import io.pivotal.trilogy.testcase.GenericTrilogyTestCase
import io.pivotal.trilogy.testcase.TestCaseHooks

class GenericStringTestCaseParser(testCaseBody: String) : BaseStringTestCaseParser(testCaseBody) {
    init {
        validate()
    }

    override fun getTestCase(): GenericTrilogyTestCase {
        return GenericTrilogyTestCase(parseDescription(), parseTests(), parseTestHooks())
    }

    private fun parseTestHooks(): TestCaseHooks {
        return TestCaseHooks(
                beforeEachTest = parseHookSection("BEFORE EACH TEST"),
                afterEachTest = parseHookSection("AFTER EACH TEST"),
                beforeAll = parseHookSection("BEFORE ALL"),
                afterAll = parseHookSection("AFTER ALL")
        )
    }

    override fun validate() {
        if (testCaseBody.hasInvalidHeader()) throw InvalidTestCaseFormat("Extra characters found in the test case header")
    }

    private fun String.hasValidHeader() : Boolean = this.contains(Regex("^# TEST CASE\\s*$", kotlin.text.RegexOption.MULTILINE))
    private fun String.hasInvalidHeader() = !this.hasValidHeader()

    private fun parseTests(): List<GenericTrilogyTest> {
        return testStrings.map { GenericStringTestParser(it).getTest() }
    }
}