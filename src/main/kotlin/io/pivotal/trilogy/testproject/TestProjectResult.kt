package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.reporting.TestCaseResult
import io.pivotal.trilogy.testcase.MalformedTrilogyTestCase

data class TestProjectResult(
        val testCaseResults: List<TestCaseResult>,
        val malformedTestCases: List<MalformedTrilogyTestCase> = emptyList(),
        val failureMessage: String? = null,
        val fatalFailure: Boolean = false) {
    val didFail: Boolean get() = ! failureMessage.isNullOrBlank()
}