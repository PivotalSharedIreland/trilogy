package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.application.TrilogyApplicationOptions
import io.pivotal.trilogy.testproject.UrlTestCaseResourceLocator
import io.pivotal.trilogy.testproject.UrlTestProjectResourceLocator
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.expect

class TrilogyApplicationOptionsTests : Spek({
    it("should provide a project resource locator") {
        val resourceLocator = TrilogyApplicationOptions(testProjectPath = "some_path").resourceLocator
        expect(true) { resourceLocator is UrlTestProjectResourceLocator }
        expect(File("some_path").toURI().toURL()) { (resourceLocator as UrlTestProjectResourceLocator).projectUrl }
    }

    it("should provide a test case resource locator") {
        expect(true) { TrilogyApplicationOptions(testCaseFilePath = "src/test/resources/testcases/sample.stt").resourceLocator is UrlTestCaseResourceLocator }
    }
})