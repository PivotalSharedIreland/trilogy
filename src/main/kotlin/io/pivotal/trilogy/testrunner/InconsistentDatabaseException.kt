package io.pivotal.trilogy.testrunner

class InconsistentDatabaseException(message: String, cause: RuntimeException) : RuntimeException(message, cause)