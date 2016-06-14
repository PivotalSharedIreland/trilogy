package io.pivotal.trilogy.testcase

import java.net.URL

class UrlTestCaseReader(val testCaseUrl: URL) : TestCaseReader{

    override fun getTestCase(): TrilogyTestCase {
        val testCaseString = testCaseUrl.readText()
        return StringTestCaseReader(testCaseString).getTestCase()
    }


}