package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.i18n.MessageCreator.createErrorMessage
import io.pivotal.trilogy.testproject.TestProjectResult
import io.pivotal.trilogy.testproject.TrilogyTestProject
import org.springframework.jdbc.BadSqlGrammarException

class DatabaseTestProjectRunner(val testCaseRunner: TestCaseRunner, val scriptExecuter: ScriptExecuter) : TestProjectRunner {

    override fun run(project: TrilogyTestProject): TestProjectResult {
        return project.runTests()
    }

    private fun TrilogyTestProject.runTests(): TestProjectResult {
        applySchema()
        runSourceScripts()
        return runTestCases()
    }

    private fun TrilogyTestProject.runSourceScripts() {
        sourceScripts.forEach { script ->
            try {
                scriptExecuter.execute(script.content)
            } catch(e: BadSqlGrammarException) {
                val errorObjects = listOf(script.name, e.localizedMessage.prependIndent("    "))
                val message = createErrorMessage("testProjectRunner.errors.scripts.invalid", errorObjects)
                throw SourceScriptLoadException(message, e)
            }
        }
    }

    private fun TrilogyTestProject.runTestCases(): TestProjectResult {
        return TestProjectResult(this.testCases.map {
            try {
                testCaseRunner.run(it, this.fixtures)
            } catch (e: UnrecoverableException) {
                throw UnrecoverableException("${it.description}:\n${e.localizedMessage.prependIndent("    ")}", e)
            }
        })
    }

    private fun TrilogyTestProject.applySchema() {
        schema?.let { scriptExecuter.execute(it) }
    }
}

