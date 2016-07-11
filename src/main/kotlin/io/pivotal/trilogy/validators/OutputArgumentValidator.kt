package io.pivotal.trilogy.validators

import io.pivotal.trilogy.testcase.TestArgumentTableTokens

class OutputArgumentValidator(val parameterNames: List<String>) {

    fun validate(expectedRow: List<String>, actualValues: Map<String, Any?>): Boolean {
        if (parameterNames.isEmpty()) return true
        return matchError(expectedRow, actualValues) ?: matchValues(actualValues, expectedRow)
    }


    private fun matchError(expectedRow: List<String>, actualValues: Map<String, Any?>): Boolean? {
        if (!parameterNames.contains(TestArgumentTableTokens.errorColumnName)) return null
        val expectedError = expectedError(expectedRow)
        if (expectedError.isBlank() && actualValues.hasNoError) return null
        if (expectedError.isBlank()) return false
        if (expectedError.toUpperCase().equals(TestArgumentTableTokens.errorWildcard)) return actualValues.containsError
        val actualError = actualValues[TestArgumentTableTokens.errorColumnName].toString().toLowerCase()
        return actualValues.containsError && actualError.contains(expectedError.toLowerCase())
    }

    private fun expectedError(expectedRow: List<String>): String {
        return expectedRow[parameterNames.indexOf(TestArgumentTableTokens.errorColumnName)]
    }

    private fun matchValues(actualValues: Map<String, Any?>, expectedRow: List<String>): Boolean {
        return expectedMap(expectedRow).equals(actualMap(actualValues))
    }


    private fun actualMap(actualValues: Map<String, Any?>): Map<String, String> {
        return actualValues.mapValues { "${it.value ?: TestArgumentTableTokens.nullMarker}" }.withoutErrors()
    }

    private fun expectedMap(expectedRow: List<String>): Map<String, String> {
        return parameterNames.zip(expectedRow).toMap().withoutErrors()
    }
    private fun <V> Map<String, V>.withoutErrors(): Map<String, V> = this.filterKeys { !it.equals(TestArgumentTableTokens.errorColumnName) }
    private val Map<String, Any?>.containsError: Boolean get() = this.containsKey(TestArgumentTableTokens.errorColumnName)
    private val Map<String, Any?>.hasNoError: Boolean get() = !this.containsError


}