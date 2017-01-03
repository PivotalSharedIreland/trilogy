package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.i18n.MessageCreator.createErrorMessage
import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.reporting.TestResult
import io.pivotal.trilogy.testcase.GenericTrilogyTest
import io.pivotal.trilogy.testcase.ProcedureTrilogyTest
import io.pivotal.trilogy.testcase.ProcedureTrilogyTestCase
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTest
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testproject.FixtureLibrary
import io.pivotal.trilogy.validators.OutputArgumentValidator

class DatabaseTestCaseRunner(val testSubjectCaller: TestSubjectCaller,
                             val assertionExecuter: AssertionExecuter, val scriptExecuter: ScriptExecuter) : TestCaseRunner {

    override fun run(trilogyTestCase: TrilogyTestCase, library: FixtureLibrary): TestCaseResult {
        trilogyTestCase.hooks.beforeAll.runSetupScripts(library)

        val testResults = trilogyTestCase.tests.map { test ->
            trilogyTestCase.hooks.beforeEachTest.runSetupScripts(library)
            val testResult = test.tryProceduralTest(library, trilogyTestCase) ?: test.tryGenericTest() ?: TestResult(test.description, "Unknown test type")
            trilogyTestCase.hooks.afterEachTest.runTeardownScripts(library)
            testResult
        }
        trilogyTestCase.hooks.afterAll.runTeardownScripts(library)

        return TestCaseResult(trilogyTestCase.description, testResults)
    }

    private fun getAssertionError(assertions: List<TrilogyAssertion>): String? {
        return assertions.map { assertion -> assertionExecuter executeReturningFailureMessage assertion }.asErrorString()
    }

    private fun GenericTrilogyTest.runTestReturningError(): String? {
        try {
            scriptExecuter.execute(this.body)
        } catch(e: RuntimeException) {
            return e.message ?: "Unknown error"
        }
        return getAssertionError(this.assertions)
    }

    private fun ProcedureTrilogyTest.runTestReturningError(testCase: ProcedureTrilogyTestCase, library: FixtureLibrary): String? {
        val outputValidator = OutputArgumentValidator(argumentTable.outputArgumentNames)

        return argumentTable.inputArgumentValues.withIndex().map { inputRowWithIndex ->

            testCase.hooks.beforeEachRow.runSetupScripts(library)
            val inputRow = inputRowWithIndex.value
            val index = inputRowWithIndex.index

            val output = try {
                testSubjectCaller.call(testCase.procedureName, argumentTable.inputArgumentNames, inputRow)
            } catch (e: InputArgumentException) {
                mapOf("=FAIL=" to e.localizedMessage)
            }

            val currentRow = inputRowWithIndex.index + 1
            val rowCount = argumentTable.inputArgumentValues.count()

            val callError = output["=FAIL="].rowCallError(currentRow, rowCount)

            val outputError = if (callError == null) outputValidator.validate(argumentTable.outputArgumentValues[index], output).rowCallError(currentRow, rowCount) else null
            val assertionError = if (callError == null) getAssertionError(assertions) else null
            testCase.hooks.afterEachRow.runTeardownScripts(library)
            listOf(callError, outputError, assertionError).asErrorString()
        }.asErrorString()
    }

    private fun List<String>.runSetupScripts(library: FixtureLibrary) = this.forEach { name -> scriptExecuter.execute(library.getSetupFixtureByName(name)) }
    private fun List<String>.runTeardownScripts(library: FixtureLibrary) = this.forEach { name -> scriptExecuter.execute(library.getTeardownFixtureByName(name)) }

    private fun TrilogyTest.tryProceduralTest(library: FixtureLibrary, trilogyTestCase: TrilogyTestCase): TestResult? {
        if (this !is ProcedureTrilogyTest) return null
        val errorMessage = this.runTestReturningError(trilogyTestCase as ProcedureTrilogyTestCase, library)
        return TestResult(this.description, errorMessage)
    }

    private fun TrilogyTest.tryGenericTest(): TestResult? {
        if (this !is GenericTrilogyTest) return null
        return TestResult(this.description, this.runTestReturningError())
    }

    private fun Iterable<String?>.asErrorString(): String? {
        val nonNullList = this.filterNotNull()
        return if (nonNullList.isNotEmpty()) nonNullList.joinToString("\n") else null
    }

    private fun Any?.rowCallError(rowNumber: Int, rowCount: Int): String? {
        return if (this != null) createErrorMessage("output.errors.forRow", listOf(rowNumber, rowCount, this)) else null
    }
}

