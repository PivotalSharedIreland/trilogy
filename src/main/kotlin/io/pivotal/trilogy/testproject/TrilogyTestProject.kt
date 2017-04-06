package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.testcase.MalformedTrilogyTestCase
import io.pivotal.trilogy.testcase.TrilogyTestCase

data class TrilogyTestProject(
        val testCases: List<TrilogyTestCase>,
        val malformedTestCases: List<MalformedTrilogyTestCase> = emptyList(),
        val sourceScripts: List<ProjectSourceScript> = emptyList(),
        val fixtures: FixtureLibrary = FixtureLibrary.emptyLibrary(),
        val schema: String? = null
)