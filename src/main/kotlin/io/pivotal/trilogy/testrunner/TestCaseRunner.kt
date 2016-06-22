package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.validators.OutputArgumentValidator

class TestCaseRunner (val testSubjectCaller: TestSubjectCaller, val assertionExecutor: AssertionExecutor) {
    fun run(trilogyTestCase: TrilogyTestCase): TestCaseResult {
        val stats = trilogyTestCase.tests.map { test ->
            runData(test.argumentTable, trilogyTestCase.procedureName) and runAssertions(test.assertions)
        }

        val numberPassed = stats.filter { it }.size
        val numberFailed = stats.filterNot { it }.size

        return TestCaseResult(numberPassed, numberFailed)
    }

    private fun runData(testArgumentTable: TestArgumentTable, procedureName : String): Boolean {
        val outputValidator = OutputArgumentValidator(testArgumentTable.outputArgumentNames)

        return testArgumentTable.inputArgumentValues.mapIndexed { index, inputArgumentValueRow ->
            val output = testSubjectCaller.call(procedureName, testArgumentTable.inputArgumentNames, inputArgumentValueRow)
            outputValidator.validate(testArgumentTable.outputArgumentValues[index], output)
        }.fold(true, { a, b -> a and b })
    }

    private fun runAssertions(assertions: List<TrilogyAssertion>): Boolean {
        return assertions.all { assertion ->
            assertionExecutor execute assertion
        }

    }
}

