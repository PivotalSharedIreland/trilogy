package io.pivotal.trilogy.testcase

import java.net.URL

class UrlTestCaseReader(val testCaseUrl: URL) : TestCaseReader {

    val testCaseString by lazy { testCaseUrl.readText() }

    override fun getTestCase(): TrilogyTestCase {
        return StringTestCaseReader(testCaseString).getTestCase()
    }


}