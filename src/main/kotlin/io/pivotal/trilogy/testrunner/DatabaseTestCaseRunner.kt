package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.testproject.FixtureLibrary
import io.pivotal.trilogy.validators.OutputArgumentValidator

class DatabaseTestCaseRunner(val testSubjectCaller: TestSubjectCaller,
                             val assertionExecutor: AssertionExecutor, val scriptExecutor: ScriptExecuter) : TestCaseRunner {

    override fun run(trilogyTestCase: TrilogyTestCase, library: FixtureLibrary): TestCaseResult {
        trilogyTestCase.hooks.beforeAll.runSetupScripts(library)

        val stats = trilogyTestCase.tests.map { test ->
            trilogyTestCase.hooks.beforeEachTest.runSetupScripts(library)
            val success = runData(test.argumentTable, trilogyTestCase, library) and runAssertions(test.assertions)
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
            assertionExecutor execute assertion
        }
    }

    private fun runData(testArgumentTable: TestArgumentTable, testCase: TrilogyTestCase, library: FixtureLibrary): Boolean {
        val outputValidator = OutputArgumentValidator(testArgumentTable.outputArgumentNames)

        return testArgumentTable.inputArgumentValues.withIndex().all { inputRowWithIndex ->

            testCase.hooks.beforeEachRow.runSetupScripts(library)
            val inputRow = inputRowWithIndex.value
            val index = inputRowWithIndex.index

            val output = testSubjectCaller.call(testCase.procedureName, testArgumentTable.inputArgumentNames, inputRow)
            val success = outputValidator.validate(testArgumentTable.outputArgumentValues[index], output)
            testCase.hooks.afterEachRow.runTeardownScripts(library)
            success
        }
    }

    private fun List<String>.runSetupScripts(library: FixtureLibrary) = this.forEach { name -> scriptExecutor.execute(library.getSetupFixtureByName(name)) }
    private fun List<String>.runTeardownScripts(library: FixtureLibrary) = this.forEach { name -> scriptExecutor.execute(library.getTeardownFixtureByName(name)) }
}

