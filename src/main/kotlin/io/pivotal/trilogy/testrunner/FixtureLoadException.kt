package io.pivotal.trilogy.testrunner

class FixtureLoadException(message: String, cause: RuntimeException) : UnrecoverableException(message, cause)