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

        trilogyTestCase.hooks.beforeAll.forEach { fixtureName ->
            scriptExecutor.execute(library.getSetupFixtureByName(fixtureName))
        }

        val stats = trilogyTestCase.tests.map { test ->
            // before each
            runData(test.argumentTable, trilogyTestCase.procedureName) and runAssertions(test.assertions)
            // after each
        }
        // after all

        val numberPassed = stats.filter { it }.size
        val numberFailed = stats.filterNot { it }.size

        return TestCaseResult(numberPassed, numberFailed)
    }

    private fun runAssertions(assertions: List<TrilogyAssertion>): Boolean {
        return assertions.all { assertion ->
            assertionExecutor execute assertion
        }
    }

    private fun runData(testArgumentTable: TestArgumentTable, functionName: String): Boolean {
        val outputValidator = OutputArgumentValidator(testArgumentTable.outputArgumentNames)

        return testArgumentTable.inputArgumentValues.withIndex().all { inputRowWithIndex ->
            val inputRow = inputRowWithIndex.value
            val index = inputRowWithIndex.index

            val output = testSubjectCaller.call(functionName, testArgumentTable.inputArgumentNames, inputRow)
            outputValidator.validate(testArgumentTable.outputArgumentValues[index], output)
        }
    }

}

