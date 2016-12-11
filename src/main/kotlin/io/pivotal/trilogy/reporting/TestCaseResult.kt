package io.pivotal.trilogy.reporting

data class TestCaseResult(val tests: List<TestResult> = emptyList()) {
    val didPass get() = failedTests.isEmpty()
    val total: Int = tests.count()
    val failedTests: List<TestResult> by lazy { tests.filter { it.hasFailed } }
    val passedTests: List<TestResult> by lazy { tests.filter { it.hasSucceeded } }
    val passed get() = passedTests.count()
    val failed get() = failedTests.count()

    infix operator fun plus(other: TestCaseResult): TestCaseResult {
        return TestCaseResult(this.tests + other.tests)
    }
}