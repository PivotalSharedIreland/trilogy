package io.pivotal.trilogy.validators

import io.pivotal.trilogy.i18n.MessageCreator.getI18nMessage
import io.pivotal.trilogy.testcase.TestArgumentTableTokens

class OutputArgumentValidator(val parameterNames: List<String>) {

    fun validate(expectedRow: List<String>, actualValues: Map<String, Any?>): String? {
        if (parameterNames.isEmpty()) return null
        val errorMismatchMessage = errorMismatchMessage(expectedRow, actualValues)
        if (errorMismatchMessage != null) return errorMismatchMessage
        if (actualValues.containsError) return null
        return matchValues(actualValues, expectedRow)
    }


    private fun errorMismatchMessage(expectedRow: List<String>, actualValues: Map<String, Any?>): String? {
        if (!parameterNames.contains(TestArgumentTableTokens.errorColumnName)) return null
        val expectedError = expectedError(expectedRow)

        if (expectedError.isBlank() && actualValues.hasNoError) return null
        if (expectedError.toUpperCase() == TestArgumentTableTokens.errorWildcard) return actualValues.wildcardErrorMessage

        val actualError = actualValues[TestArgumentTableTokens.errorColumnName]
        if (actualError != null) {
            if (expectedError.isBlank()) return getI18nMessage("assertions.errors.unexpected", listOf(actualError))
            if (actualValues.containsError && actualError.toString().toUpperCase().contains(expectedError.toUpperCase())) return null
            return getI18nMessage("assertions.errors.mismatch", listOf(expectedError, actualError))
        } else {
            return getI18nMessage("assertions.errors.absent.specific", listOf(expectedError))
        }
    }


    private fun expectedError(expectedRow: List<String>): String {
        return expectedRow[parameterNames.indexOf(TestArgumentTableTokens.errorColumnName)]
    }

    private fun matchValues(actualValues: Map<String, Any?>, expectedRow: List<String>): String? {
        val expectedRowMap = expectedMap(expectedRow)
        val actualRowMap = actualMap(actualValues)
        return if (expectedRowMap == actualRowMap) null else describeDifference(expectedRowMap, actualRowMap)
    }

    private fun describeDifference(expectedRow: Map<String, String>, actualRow: Map<String, String>): String? {
        return actualRow.filterNot { (k, v) -> v == expectedRow[k] }.map { (k, v) -> getI18nMessage("output.errors.mismatch", listOf(expectedRow[k] as String, k, v))  }.joinToString("\n")
    }


    private fun actualMap(actualValues: Map<String, Any?>): Map<String, String> {
        return actualValues.mapValues { "${it.value ?: TestArgumentTableTokens.nullMarker}" }.withoutErrors()
    }

    private fun expectedMap(expectedRow: List<String>): Map<String, String> {
        return parameterNames.zip(expectedRow).toMap().withoutErrors()
    }

    private fun <V> Map<String, V>.withoutErrors(): Map<String, V> = this.filterKeys { it != TestArgumentTableTokens.errorColumnName }
    private val Map<String, Any?>.containsError: Boolean get() = this.containsKey(TestArgumentTableTokens.errorColumnName)
    private val Map<String, Any?>.hasNoError: Boolean get() = !this.containsError
    private val Map<String, Any?>.wildcardErrorMessage: String? get() = if (this.containsError) null else getI18nMessage("assertions.errors.absent.any", emptyList())
}

