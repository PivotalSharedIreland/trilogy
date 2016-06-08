package io.pivotal.trilogy.testcase

data class Execution(val inputArguments: Map<String, String>, val outputArguments: Map<String, String>, val expectedError: String?)