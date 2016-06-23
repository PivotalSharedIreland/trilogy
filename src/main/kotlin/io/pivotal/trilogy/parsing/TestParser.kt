package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.TrilogyTest

interface TestParser {
    fun getTest(): TrilogyTest
}