package io.pivotal.trilogy.testrunner

class SchemaLoadFailedException(message: String, exception: RuntimeException) : UnrecoverableException(message, exception)