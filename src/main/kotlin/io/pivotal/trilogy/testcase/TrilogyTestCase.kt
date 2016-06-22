package io.pivotal.trilogy.testcase

data class TrilogyTestCase(val procedureName: String, val description: String, val tests: List<TrilogyTest>)

