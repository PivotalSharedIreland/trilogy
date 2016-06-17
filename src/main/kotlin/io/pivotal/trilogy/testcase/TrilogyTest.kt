package io.pivotal.trilogy.testcase

data class TrilogyTest(val description: String, val argumentTable: TestArgumentTable, val assertions: List<TrilogyAssertion>)