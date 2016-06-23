package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.TrilogyTestCase

interface TestCaseParser {
    fun getTestCase(): TrilogyTestCase
}

