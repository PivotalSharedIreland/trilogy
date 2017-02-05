package io.pivotal.trilogy.testrunner

open class UnrecoverableException(message: String, cause: RuntimeException) : RuntimeException(message, cause)