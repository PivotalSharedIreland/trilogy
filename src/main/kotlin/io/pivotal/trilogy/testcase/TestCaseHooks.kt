package io.pivotal.trilogy.testcase

data class TestCaseHooks(val beforeAll: List<String>, val beforeEach: List<String>, val afterAll: List<String>, val afterEach: List<String>)