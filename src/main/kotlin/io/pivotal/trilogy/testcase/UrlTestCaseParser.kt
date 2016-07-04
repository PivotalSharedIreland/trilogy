package io.pivotal.trilogy.testcase

import java.net.URL

class UrlTestCaseParser(val testCaseUrl: URL) : TestCaseParser {

    val testCaseString by lazy { testCaseUrl.readText() }

    override fun getTestCase(): TrilogyTestCase {
        return StringTestCaseParser(testCaseString).getTestCase()
    }


}
