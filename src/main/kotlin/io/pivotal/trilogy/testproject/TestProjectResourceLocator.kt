package io.pivotal.trilogy.testproject

interface TestProjectResourceLocator {
    val testCases: List<String>
    val testsPresent: Boolean get() = testCases.isNotEmpty()
    val testsAbsent: Boolean get() = !testsPresent
    val schema: String? get() = null
    val sourceScripts: List<ProjectSourceScript> get() = emptyList()
    val setupFixtures: List<NamedStringResource> get() = emptyList()
    val teardownFixtures: List<NamedStringResource> get() = emptyList()
}