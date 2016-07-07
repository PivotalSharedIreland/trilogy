package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTest
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testproject.FixtureLibrary
import io.pivotal.trilogy.validators.OutputArgumentValidator

class DatabaseTestCaseRunner(val testSubjectCaller: TestSubjectCaller,
                             val assertionExecuter: AssertionExecuter, val scriptExecuter: ScriptExecuter) : TestCaseRunner {

    override fun run(trilogyTestCase: TrilogyTestCase, library: FixtureLibrary): TestCaseResult {
        trilogyTestCase.hooks.beforeAll.runSetupScripts(library)

        val stats = trilogyTestCase.tests.map { test ->
            trilogyTestCase.hooks.beforeEachTest.runSetupScripts(library)
            val success = test.runData(trilogyTestCase, library)
            trilogyTestCase.hooks.afterEachTest.runTeardownScripts(library)
            success
        }
        trilogyTestCase.hooks.afterAll.runTeardownScripts(library)

        val numberPassed = stats.filter { it }.size
        val numberFailed = stats.filterNot { it }.size

        return TestCaseResult(numberPassed, numberFailed)
    }

    private fun runAssertions(assertions: List<TrilogyAssertion>): Boolean {
        return assertions.all { assertion ->
            assertionExecuter execute assertion
        }
    }

    private fun TrilogyTest.runData(testCase: TrilogyTestCase, library: FixtureLibrary): Boolean {
        val outputValidator = OutputArgumentValidator(argumentTable.outputArgumentNames)

        return argumentTable.inputArgumentValues.withIndex().map { inputRowWithIndex ->

            testCase.hooks.beforeEachRow.runSetupScripts(library)
            val inputRow = inputRowWithIndex.value
            val index = inputRowWithIndex.index

            val assertionSuccess = runAssertions(assertions)

            val output = testSubjectCaller.call(testCase.procedureName, argumentTable.inputArgumentNames, inputRow)
            val outputSuccess = outputValidator.validate(argumentTable.outputArgumentValues[index], output)
            testCase.hooks.afterEachRow.runTeardownScripts(library)
            outputSuccess && assertionSuccess
        }.all { it }
    }

    private fun List<String>.runSetupScripts(library: FixtureLibrary) = this.forEach { name -> scriptExecuter.execute(library.getSetupFixtureByName(name)) }
    private fun List<String>.runTeardownScripts(library: FixtureLibrary) = this.forEach { name -> scriptExecuter.execute(library.getTeardownFixtureByName(name)) }
}

