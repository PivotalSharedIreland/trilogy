package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.testcase.MalformedTrilogyTestCase

data class TrilogyRunResult(val projectResult: TestProjectResult, val malformedTrilogyTestCases: List<MalformedTrilogyTestCase> = emptyList())