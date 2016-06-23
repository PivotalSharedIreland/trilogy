package io.pivotal.trilogy

import io.pivotal.trilogy.parsing.StringTestCaseParser
import io.pivotal.trilogy.testcase.TrilogyTestCase

object Fixtures {
    fun getTestCase(testCaseName: String): TrilogyTestCase {
        return StringTestCaseParser(ResourceHelper.getTestCaseByName(testCaseName)).getTestCase()
    }
}
