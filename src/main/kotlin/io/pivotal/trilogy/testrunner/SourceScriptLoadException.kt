package io.pivotal.trilogy.testrunner

class SourceScriptLoadException(message: String, cause: RuntimeException) : UnrecoverableException(message, cause)