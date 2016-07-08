package io.pivotal.trilogy.application

import io.pivotal.trilogy.testproject.TestProjectResourceLocator
import io.pivotal.trilogy.testproject.UrlTestCaseResourceLocator
import io.pivotal.trilogy.testproject.UrlTestProjectResourceLocator
import java.io.File

data class TrilogyApplicationOptions(val testCaseFilePath: String? = null, val testProjectPath: String? = null, override val shouldSkipSchema: Boolean = false) :TrilogyOptions {
    override val resourceLocator: TestProjectResourceLocator by lazy {
        if (testCaseFilePath.isNullOrEmpty())
            UrlTestProjectResourceLocator(File(testProjectPath).toURI().toURL())
        else
            UrlTestCaseResourceLocator(File(testCaseFilePath).toURI().toURL())
    }
}