package io.pivotal.trilogy.application

import io.pivotal.trilogy.testproject.TestProjectResourceLocator

interface TrilogyOptions {
    val resourceLocator: TestProjectResourceLocator
    val shouldSkipSchema: Boolean
}