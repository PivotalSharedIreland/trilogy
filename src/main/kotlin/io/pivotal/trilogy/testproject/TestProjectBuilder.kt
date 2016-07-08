package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.application.TrilogyOptions
import io.pivotal.trilogy.testcase.StringTestCaseParser

object TestProjectBuilder {
    fun build(options: TrilogyOptions): TrilogyTestProject {
        if (options.resourceLocator.testsAbsent) throw UnsupportedOperationException()
        return TrilogyTestProject(
                options.resourceLocator.testCases.map { StringTestCaseParser(it).getTestCase() },
                sourceScripts = options.resourceLocator.sourceScripts,
                fixtures = options.resourceLocator.fixtures(),
                schema = if (options.shouldSkipSchema) null else options.resourceLocator.schema
        )
    }

    private fun TestProjectResourceLocator.fixtures(): FixtureLibrary {
        fun foldIntoMapWithPrefix(prefix: String): (Map<String, String>, NamedStringResource) -> Map<String, String> {
            return { map, fixture -> map + mapOf(Pair(prefix + fixture.name, fixture.content)) }
        }

        val setupFixtureMap = this.setupFixtures.fold(emptyMap<String, String>(), foldIntoMapWithPrefix("setup/"))
        val fullFixtureMap = this.teardownFixtures.fold(setupFixtureMap, foldIntoMapWithPrefix("teardown/"))
        return FixtureLibrary(fullFixtureMap)
    }
}