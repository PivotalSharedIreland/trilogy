package io.pivotal.trilogy.reporting

data class TestCaseReport(val passed: Int, val failed: Int) {
    val didPass: Boolean = failed == 0
}