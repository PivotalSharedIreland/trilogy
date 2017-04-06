package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.test_helpers.ResourceHelper
import io.pivotal.trilogy.test_helpers.shouldStartWith
import io.pivotal.trilogy.test_helpers.shouldThrow
import org.amshove.kluent.AnyException
import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class UrlTestCaseResourceLocatorTests : Spek({
    it("should be instantiated using a testcase file URL") {
        val locator = UrlTestCaseResourceLocator(ResourceHelper.getResourceUrl("/testcases/degenerate.stt"))
        expect(true) { locator.testsPresent }
        expect(1) { locator.testCases.count() }
        locator.testCases.first().body shouldStartWith "# TEST CASE DEGENERATE"
    }

    it("crashes when the supplied URL is not for a test case") {
        { UrlTestCaseResourceLocator(ResourceHelper.getResourceUrl("/tables/completeTable.md")) } shouldThrow AnyException
    }

    it("crashes when the file does not exist") {
        { UrlTestCaseResourceLocator(ResourceHelper.getResourceUrl("/testcases/degenerate_thing.stt")) } shouldThrow AnyException
    }
})