package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.reporting.TestCaseResult

data class TestProjectResult(val testCaseResults: List<TestCaseResult>, val failureMessage: String? = null, val fatalFailure: Boolean = false) {
    val didFail: Boolean get() = ! failureMessage.isNullOrBlank()
}