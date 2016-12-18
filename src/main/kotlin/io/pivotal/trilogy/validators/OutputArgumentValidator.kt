package io.pivotal.trilogy.validators

import io.pivotal.trilogy.testcase.TestArgumentTableTokens
import org.springframework.context.i18n.LocaleContextHolder
import java.text.MessageFormat
import java.util.ResourceBundle

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
            if (expectedError.isBlank()) return createErrorMessage("assertions.errors.unexpected", listOf(actualError))
            if (actualValues.containsError && actualError.toString().toUpperCase().contains(expectedError.toUpperCase())) return null
            return createErrorMessage("assertions.errors.mismatch", listOf(expectedError, actualError))
        } else {
            return createErrorMessage("assertions.errors.absent.specific", listOf(expectedError))
        }
    }


    private fun expectedError(expectedRow: List<String>): String {
        return expectedRow[parameterNames.indexOf(TestArgumentTableTokens.errorColumnName)]
    }

    private fun matchValues(actualValues: Map<String, Any?>, expectedRow: List<String>): String? {
        return if (expectedMap(expectedRow) == actualMap(actualValues)) null else "value mismatch"
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
    private val Map<String, Any?>.wildcardErrorMessage: String? get() = if (this.containsError) null else createErrorMessage("assertions.errors.absent.any", emptyList())

    private fun createErrorMessage(messagePath: String, messageArguments: List<Any>) = MessageFormat(getI18nMessage(messagePath)).format(messageArguments.toTypedArray())
    private fun getI18nMessage(name: String): String = ResourceBundle.getBundle("messages", LocaleContextHolder.getLocale()).getString(name)
}