package io.pivotal.trilogy.testcase

import io.pivotal.trilogy.Fixtures
import io.pivotal.trilogy.ResourceHelper
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect

class UrlTestCaseReaderTests : Spek({

    it("reads an existing file into a test case") {
        val testCase = Fixtures.getTestCase("degenerate")
        expect (testCase) { UrlTestCaseReader(ResourceHelper.getResourceUrl("/testcases/degenerate.stt")).getTestCase() }
    }

    it("fails for non-readable files") {
        assertFails { UrlTestCaseReader(ResourceHelper.getResourceUrl("/foo/bar.stt")).getTestCase() }
    }

})

