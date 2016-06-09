package io.pivotal.trilogy.testcase

data class TrilogyTestCase(val functionName: String, val description: String, val tests: List<TrilogyTest>)

