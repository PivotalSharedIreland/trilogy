package io.pivotal.trilogy.reporting

data class TestCaseResult(val passed: Int = 0, val failed: Int = 0) {
    val didPass: Boolean = failed == 0
    val total: Int = failed + passed
}