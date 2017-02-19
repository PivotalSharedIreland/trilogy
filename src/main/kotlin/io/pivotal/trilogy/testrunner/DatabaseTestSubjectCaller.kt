package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.i18n.MessageCreator.getI18nMessage
import io.pivotal.trilogy.testcase.TestArgumentTableTokens
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import java.util.HashMap
import javax.sql.DataSource

class DatabaseTestSubjectCaller(@Autowired val dataSource: DataSource) : TestSubjectCaller {

    override fun call(procedureName: String, parameterNames: List<String>, parameterValues: List<String>): Map<String, Any?> {
        val jdbcCall = jdbcCall(procedureName)
        return jdbcCall.safeExecute(inputParameters(parameterNames, parameterValues))
    }

    private fun jdbcCall(procedureName: String): SimpleJdbcCall {
        return SimpleJdbcCall(dataSource).apply {
            withProcedureName(procedureName)
        }
    }

    private fun inputParameters(parameterNames: List<String>, parameterValues: List<String>): Map<String, String?> {
        return HashMap<String, String?>().apply {
            parameterNames.forEachIndexed { index, name ->
                if (parameterValues[index].isValuePresent()) put(name, parameterValues[index]) else put(name, null)
            }
        }
    }

    private fun String.isNullValue() = equals(TestArgumentTableTokens.nullMarker)
    private fun String.isValuePresent() = !isNullValue()

    private fun SimpleJdbcCall.safeExecute(parameters: Map<String, String?>): Map<String, Any?> {
        verifyInputParameters(parameters)
        return try {
            this.execute(parameters)
        } catch (e: InvalidDataAccessApiUsageException) {
          throw MissingArgumentException(e.localizedMessage, e)
        } catch (e: DataAccessException) {
            mapOf(Pair(TestArgumentTableTokens.errorColumnName, e.cause?.message ?: e.message))
        } catch (e: NumberFormatException) {
            throw InputArgumentException(getI18nMessage("testSubjectCaller.errors.mismatch.input.numeric", listOf(parameters.dumpInput)), e)
        } catch (e: IllegalArgumentException) {
            throw InputArgumentException(getI18nMessage("testSubjectCaller.errors.mismatch.input.datetime", listOf(parameters.dumpInput, e.localizedMessage)), e)
        }
    }

    private fun SimpleJdbcCall.verifyInputParameters(parameters: Map<String, String?>) {
        val callParameters = parameters.keys - setOf(TestArgumentTableTokens.errorColumnName)
        val unknownArguments = callParameters - validArguments(callParameters)
        if (unknownArguments.isNotEmpty())
            throw UnexpectedArgumentException(getI18nMessage("testSubjectCaller.errors.mismatch.input.unexpected", listOf(unknownArguments.joinToString(", "))),
                    RuntimeException("Unexpected arguments"))
    }

    private fun SimpleJdbcCall.validArguments(callParameters: Set<String>): Set<String> {
        this.withNamedBinding()
        this.useInParameterNames(*(callParameters.toTypedArray()))
        this.compile()
        val validArguments = Regex("\\W?(\\w+)\\s+=>").findAll(this.callString).map { it.groups[1]?.value }.filterNotNull().toSet()
        return validArguments
    }

    val Map<String, String?>.dumpInput: String
        get() = this.filterKeys { !it.endsWith("$") }.map { (k, v) -> "    $k => $v"  }.joinToString("\n")
}
