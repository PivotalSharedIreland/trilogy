package io.pivotal.trilogy.testproject

interface TestProjectResourceLocator {
    val testsPresent: Boolean
    val testsAbsent: Boolean get() = !testsPresent
    val schema: String?
    val testCases: List<String>
    val sourceScripts: List<String>
    val setupFixtures: List<NamedStringResource>
    val teardownFixtures: List<NamedStringResource>
}