package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.application.TrilogyOptions
import io.pivotal.trilogy.parsing.GenericStringTestCaseParser
import io.pivotal.trilogy.parsing.ProcedureStringTestCaseParser
import io.pivotal.trilogy.testcase.TrilogyTestCase

object TestProjectBuilder {
    fun build(options: TrilogyOptions): TrilogyTestProject {
        if (options.resourceLocator.testsAbsent) throw UnsupportedOperationException("No tests were found")
        return TrilogyTestProject(
                extractTestCases(options),
                sourceScripts = options.resourceLocator.sourceScripts,
                fixtures = options.resourceLocator.fixtures(),
                schema = if (options.shouldSkipSchema) null else options.resourceLocator.schema
        )
    }

    private fun extractTestCases(options: TrilogyOptions): List<TrilogyTestCase> {
        return options.resourceLocator.testCases.map { extractTestCase(it) }
    }

    private fun extractTestCase(testCase: String): TrilogyTestCase {
        return try {
            ProcedureStringTestCaseParser(testCase).getTestCase()
        } catch (e: RuntimeException) {
            GenericStringTestCaseParser(testCase).getTestCase()
        }
    }

    private fun TestProjectResourceLocator.fixtures(): FixtureLibrary {
        fun foldIntoMapWithPrefix(prefix: String): (Map<String, String>, NamedStringResource) -> Map<String, String> {
            return { map, (name, content) -> map + mapOf(Pair(prefix + name, content)) }
        }

        val setupFixtureMap = this.setupFixtures.fold(emptyMap<String, String>(), foldIntoMapWithPrefix("setup/"))
        val fullFixtureMap = this.teardownFixtures.fold(setupFixtureMap, foldIntoMapWithPrefix("teardown/"))
        return FixtureLibrary(fullFixtureMap)
    }
}