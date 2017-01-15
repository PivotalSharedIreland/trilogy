package io.pivotal.trilogy.reporting

data class TestCaseResult(val testCaseName: String, val tests: List<TestResult> = emptyList(), val errorMessage: String? = null) {
    val didPass: Boolean get() = failedTests.isEmpty()
    val total: Int = tests.count()
    val failedTests: List<TestResult> by lazy { tests.filter { it.hasFailed } }
    val passedTests: List<TestResult> by lazy { tests.filter { it.hasSucceeded } }
    val passed: Int get() = passedTests.count()
    val failed: Int get() = failedTests.count()
}