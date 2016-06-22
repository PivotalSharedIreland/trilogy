package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.TestArgumentTable
import io.pivotal.trilogy.testcase.TrilogyAssertion
import io.pivotal.trilogy.testcase.TrilogyTestCase
import io.pivotal.trilogy.validators.OutputArgumentValidator
import org.springframework.beans.factory.annotation.Autowired

class DatabaseTestCaseRunner(@Autowired val testSubjectCaller: TestSubjectCaller,
                             @Autowired val assertionExecutor: AssertionExecutor) : TestCaseRunner {

    override fun run(trilogyTestCase: TrilogyTestCase): TestCaseResult {
        val stats = trilogyTestCase.tests.map { test ->
            runData(test.argumentTable, trilogyTestCase.procedureName) and runAssertions(test.assertions)
        }

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

        return testArgumentTable.inputArgumentValues.mapIndexed { index, inputRow ->
            val output = testSubjectCaller.call(functionName, testArgumentTable.inputArgumentNames, inputRow)
            outputValidator.validate(testArgumentTable.outputArgumentValues[index], output)
        }.fold(true, { a, b -> a and b })
    }

}

