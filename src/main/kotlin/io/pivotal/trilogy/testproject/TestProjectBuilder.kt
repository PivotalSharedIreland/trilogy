package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.testcase.StringTestCaseParser

object TestProjectBuilder {
    fun build(resourceLocator: TestProjectResourceLocator): TrilogyTestProject {
        if (resourceLocator.testsAbsent) throw UnsupportedOperationException()
        return TrilogyTestProject(
                resourceLocator.testCases.map { StringTestCaseParser(it).getTestCase() },
                sourceScripts = resourceLocator.sourceScripts,
                fixtures = resourceLocator.fixtures(),
                schema = resourceLocator.schema
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