package io.pivotal.trilogy

import io.pivotal.trilogy.testcase.StringTestCaseReader
import io.pivotal.trilogy.testcase.TrilogyTestCase

object Fixtures {
    fun getTestCase(testCaseName: String): TrilogyTestCase {
        return StringTestCaseReader(ResourceHelper.getTestCaseByName(testCaseName)).getTestCase()
    }
}