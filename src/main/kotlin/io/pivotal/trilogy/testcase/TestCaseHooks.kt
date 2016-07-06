package io.pivotal.trilogy.testcase

data class TestCaseHooks(
        val beforeAll: List<String> = emptyList(),
        val beforeEachTest: List<String> = emptyList(),
        val beforeEachRow: List<String> = emptyList(),
        val afterAll: List<String> = emptyList(),
        val afterEachTest: List<String> = emptyList(),
        val afterEachRow: List<String> = emptyList()
)