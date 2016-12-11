package io.pivotal.trilogy.reporting

data class TestResult(val testName: String, val errorMessage: String? = null) {
    val hasSucceeded: Boolean get() = errorMessage.isNullOrBlank()
    val hasFailed: Boolean get() = !this.hasSucceeded
}