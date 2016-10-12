package io.pivotal.trilogy.testcase

data class ProcedureTrilogyTestCase(val procedureName: String, override val description: String,
                                    override val tests: List<TrilogyTest>,
                                    override val hooks: TestCaseHooks) : TrilogyTestCase