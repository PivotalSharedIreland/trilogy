package io.pivotal.trilogy.mocks

import io.pivotal.trilogy.application.TrilogyOptions
import io.pivotal.trilogy.testproject.TestProjectResourceLocator

class TrilogyApplicationOptionsStub(override val shouldSkipSchema: Boolean = false) : TrilogyOptions {
    var locator: TestProjectResourceLocator? = null
    override val resourceLocator: TestProjectResourceLocator get() = locator!!
}