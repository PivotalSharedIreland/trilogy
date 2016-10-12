package io.pivotal.trilogy.testcase

interface TrilogyTest {
    val description: String
    val assertions: List<TrilogyAssertion>
}