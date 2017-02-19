package io.pivotal.trilogy.testcase

data class MalformedProcedureTrilogyTest(override val description: String,
                                         override val assertions: List<TrilogyAssertion> = emptyList(),
                                         val errorMessage: String) : ProcedureTrilogyTest