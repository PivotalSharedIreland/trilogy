package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.TestCaseParser
import io.pivotal.trilogy.testcase.TrilogyTestCase
import java.net.URL

class UrlTestCaseParser(val testCaseUrl: URL) : TestCaseParser {

    val testCaseString by lazy { testCaseUrl.readText() }

    override fun getTestCase(): TrilogyTestCase {
        return ProcedureStringTestCaseParser(testCaseString).getTestCase()
    }


}
