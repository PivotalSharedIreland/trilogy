package io.pivotal.trilogy.testproject

import java.io.File
import java.net.URL

class UrlTestCaseResourceLocator(url: URL) : TestProjectResourceLocator {
    init {
        if (url.isInvalid) throw UnsupportedOperationException()
    }
    override val testCases = listOf(url.textContent)

    private val URL.isInvalid: Boolean get() = !isValid
    private val URL.isValid: Boolean get() = file.toLowerCase().endsWith(".stt")
    private val URL.textContent: String get() = File(toURI()).readText()
}