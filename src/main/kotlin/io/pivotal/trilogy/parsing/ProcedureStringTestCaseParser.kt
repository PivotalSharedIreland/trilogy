package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.parsing.exceptions.test.BaseParseException
import io.pivotal.trilogy.parsing.exceptions.testcase.InvalidFormat
import io.pivotal.trilogy.parsing.exceptions.testcase.TypeMismatch
import io.pivotal.trilogy.testcase.MalformedTrilogyTest
import io.pivotal.trilogy.testcase.ProcedureTrilogyTest
import io.pivotal.trilogy.testcase.ProcedureTrilogyTestCase
import io.pivotal.trilogy.testcase.TestCaseHooks
import io.pivotal.trilogy.testcase.TrilogyTestCase

class ProcedureStringTestCaseParser(testCaseBody: String) : BaseStringTestCaseParser(testCaseBody) {

    class MissingFunctionName(message: String?) : RuntimeException(message)

    override val testCaseHeaderRegex = Regex("^# TEST CASE (\\S*)\\s+")

    init {
        validate()
    }

    override fun getTestCase(): TrilogyTestCase {
        return parse()
    }

    override fun validate() {
        if (!testCaseBody.isAProceduralTestCase()) throw TypeMismatch("Not a procedural test case")
        if (!testCaseBody.isValidTestCase()) throw InvalidFormat("Unable to recognise a test case")
    }

    private fun parse(): TrilogyTestCase {
        return ProcedureTrilogyTestCase(parseFunctionName(), parseDescription(), parseTests(), parseHooks(), malformedTests())
    }

    private fun parseFunctionName(): String {
        val functionName = testCaseHeaderRegex.find(testCaseBody)!!.groupValues[1].trim()
        if (functionName.isEmpty()) throw MissingFunctionName("A test case should specify a function name for testing")
        return functionName
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

    private fun String.isValidTestCase(): Boolean {
        return hasValidHeader() && hasValidTest()
    }

    private fun String.isAProceduralTestCase(): Boolean {
        return hasValidHeader()
    }

    private fun String.hasValidHeader() = this.contains(testCaseHeaderRegex)

    private fun parseTests(): List<ProcedureTrilogyTest> {
        return testStrings.map {
            try {
                ProcedureStringTestParser(it).getTest()
            } catch (e: BaseParseException) {
                null
            }
        }.filterNotNull()
    }

    private fun malformedTests(): List<MalformedTrilogyTest> {
        return testStrings.map {
            try {
                ProcedureStringTestParser(it).getTest()
                null
            } catch (e: BaseParseException) {
                MalformedTrilogyTest(e.testName, e.localizedMessage)
            }
        }.filterNotNull()
    }

}

