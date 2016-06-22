package io.pivotal.trilogy.reporting

data class TestCaseResult(val passed: Int = 0, val failed: Int = 0) {
    val didPass: Boolean = failed == 0
    val total: Int = failed + passed

    infix operator fun plus(other: TestCaseResult): TestCaseResult {
        return TestCaseResult(this.passed + other.passed, this.failed + other.failed)
    }
}