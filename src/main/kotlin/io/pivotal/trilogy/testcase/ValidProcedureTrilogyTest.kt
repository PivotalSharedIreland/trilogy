package io.pivotal.trilogy.testcase

data class ValidProcedureTrilogyTest(override val description: String, val argumentTable: TestArgumentTable,
                                     override val assertions: List<TrilogyAssertion>) : ProcedureTrilogyTest