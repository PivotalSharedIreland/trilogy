package io.pivotal.trilogy.testrunner

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
            val success = test.tryProcedural(library, trilogyTestCase) ?: test.tryGeneric() ?: false
            val errorMessage = if (success) null else "error"
            trilogyTestCase.hooks.afterEachTest.runTeardownScripts(library)
            TestResult(test.description, errorMessage)
        }
        trilogyTestCase.hooks.afterAll.runTeardownScripts(library)

        return TestCaseResult(trilogyTestCase.description, testResults)
    }

    private fun runAssertions(assertions: List<TrilogyAssertion>): Boolean {
        return assertions.all { assertion -> assertionExecuter execute assertion }
    }

    private fun GenericTrilogyTest.runTest(): Boolean {
        try {
            scriptExecuter.execute(this.body)
        } catch(e: RuntimeException) {
            return false
        }
        return runAssertions(this.assertions)
    }

    private fun ProcedureTrilogyTest.runTest(testCase: ProcedureTrilogyTestCase, library: FixtureLibrary): Boolean {
        val outputValidator = OutputArgumentValidator(argumentTable.outputArgumentNames)

        return argumentTable.inputArgumentValues.withIndex().map { inputRowWithIndex ->

            testCase.hooks.beforeEachRow.runSetupScripts(library)
            val inputRow = inputRowWithIndex.value
            val index = inputRowWithIndex.index

            val output = testSubjectCaller.call(testCase.procedureName, argumentTable.inputArgumentNames, inputRow)

            val outputSuccess = outputValidator.validate(argumentTable.outputArgumentValues[index], output)
            val assertionSuccess = runAssertions(assertions)
            testCase.hooks.afterEachRow.runTeardownScripts(library)
            outputSuccess && assertionSuccess
        }.all { it }
    }

    private fun List<String>.runSetupScripts(library: FixtureLibrary) = this.forEach { name -> scriptExecuter.execute(library.getSetupFixtureByName(name)) }
    private fun List<String>.runTeardownScripts(library: FixtureLibrary) = this.forEach { name -> scriptExecuter.execute(library.getTeardownFixtureByName(name)) }

    private fun TrilogyTest.tryProcedural(library: FixtureLibrary, trilogyTestCase: TrilogyTestCase): Boolean? {
        return (this as? ProcedureTrilogyTest)?.runTest(trilogyTestCase as ProcedureTrilogyTestCase, library)
    }

    private fun TrilogyTest.tryGeneric(): Boolean? {
        return (this as? GenericTrilogyTest)?.runTest()
    }
}

