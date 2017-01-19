package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.test_helpers.ResourceHelper
import io.pivotal.trilogy.test_helpers.shouldStartWith
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class UrlTestProjectResourceLocatorTests : Spek({

    context("Query methods") {
        data class QueryTestScenario(val title: String, val location: String, val testsPresent: Boolean, val schemaPresent: Boolean, val testStringsPresent: Boolean, val sourceScriptsPresent: Boolean, val setupFixturesPresent: Boolean, val teardownFixturesPresent: Boolean) {
            val testsAbsent = !testsPresent

            val testPresenceTitle: String get() = "should note the ${if (testsPresent) "presence" else "absence"} of tests"
            val schemaPresenceTitle: String get() = "should ${if (schemaPresent) "" else "not "}point to a schema"
            val testCasesPresenceTitle: String get() = "${presenceTitleFor(testStringsPresent)} test cases"
            val sourceScriptsPresenceTitle: String get() = "${presenceTitleFor(sourceScriptsPresent)} sources"
            val setupFixtureFilesPresenceTitle: String get() = "${presenceTitleFor(setupFixturesPresent)} setup fixtures"
            val teardownFixtureFilesPresenceTitle: String get() = "${presenceTitleFor(teardownFixturesPresent)} teardown fixtures"

            private fun presenceTitleFor(item: Boolean) = if (item) "should have some" else "should have no"
        }

        val tests = listOf(
                QueryTestScenario("Blank project", "blank", testsPresent = false, schemaPresent = false, testStringsPresent = false, sourceScriptsPresent = false, setupFixturesPresent = false, teardownFixturesPresent = false),
                QueryTestScenario("Project with no tests", "no_tests", testsPresent = false, schemaPresent = false, testStringsPresent = false, sourceScriptsPresent = false, setupFixturesPresent = false, teardownFixturesPresent = false),
                QueryTestScenario("Project with one test", "single_testcase", testsPresent = true, schemaPresent = false, testStringsPresent = true, sourceScriptsPresent = false, setupFixturesPresent = false, teardownFixturesPresent = false),
                QueryTestScenario("Project with schema", "schema", testsPresent = true, schemaPresent = true, testStringsPresent = true, sourceScriptsPresent = true, setupFixturesPresent = false, teardownFixturesPresent = false),
                QueryTestScenario("Project with fixtures", "setup_teardown", testsPresent = true, schemaPresent = true, testStringsPresent = true, sourceScriptsPresent = true, setupFixturesPresent = true, teardownFixturesPresent = true),
                QueryTestScenario("Project with nested structure", "nested", testsPresent = true, schemaPresent = false, testStringsPresent = true, sourceScriptsPresent = true, setupFixturesPresent = true, teardownFixturesPresent = true)
        )


        tests.forEach { test ->
            context(test.title) {
                fun listPresenceTest(description: String, shouldBePresent: Boolean, testedFileList: List<Any>) {
                    it(description) { expect(shouldBePresent) { testedFileList.isNotEmpty() } }
                }

                val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/${test.location}/"))

                it(test.testPresenceTitle) {
                    expect(test.testsPresent) { subject.testsPresent }
                    expect(test.testsAbsent) { subject.testsAbsent }
                }

                it(test.schemaPresenceTitle) {
                    expect(test.schemaPresent) { subject.schema != null }
                }

                listPresenceTest(test.testCasesPresenceTitle, test.testStringsPresent, subject.testCases)
                listPresenceTest(test.sourceScriptsPresenceTitle, test.sourceScriptsPresent, subject.sourceScripts)
                listPresenceTest(test.setupFixtureFilesPresenceTitle, test.setupFixturesPresent, subject.setupFixtures)
                listPresenceTest(test.teardownFixtureFilesPresenceTitle, test.setupFixturesPresent, subject.teardownFixtures)
            }
        }
    }

    describe("Listing resources") {
        describe("source scripts") {
            it("should list source files from a flat directory") {
                val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/schema/"))
                expect(1) { subject.sourceScripts.count() }
            }

            it("should only include sql files") {
                val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/multiple_testcases/"))
                expect(2) { subject.sourceScripts.count() }
            }

            describe("nested project") {
                val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/nested/"))

                it("should include all scripts") {
                    expect(4) { subject.sourceScripts.count() }
                }

                it("should order the scripts by folder/file name") {
                    listOf(
                            "CREATE OR REPLACE PROCEDURE EXAMPLE$",
                            "CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE",
                            "CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURES",
                            "CREATE OR REPLACE PROCEDURE SUBJECT_EXAMPLE$"
                    ).forEachIndexed { i, snippet -> subject.sourceScripts[i].content shouldStartWith snippet }
                }
            }

        }

        describe("test cases") {
            describe("Multiple test cases project") {
                val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/multiple_testcases/"))
                it("should list the test cases") {
                    expect(2) { subject.testCases.count() }
                }

                it("should order the test cases by filename") {
                    subject.testCases.first() shouldStartWith "# TEST CASE DEGENERATE"
                    subject.testCases.last() shouldStartWith "# TEST CASE EXAMPLE_PROCEDURE"
                }
            }
            it("should only list test cases from the appropriate directory") {
                val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/no_tests/"))
                expect(0) { subject.testCases.count() }
            }

            it("should list nested test cases") {
                val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/nested/"))
                expect(4) { subject.testCases.count() }
            }
        }

        describe("fixtures") {
            data class FixtureCountScenario(val projectName: String, val setupFixtureCount: Int, val teardownFixtureCount: Int, val testLabel: String) {
                val setupTestLabel = "should ${testLabel.replace("#", "setup")}"
                val teardownTestLabel = "should ${testLabel.replace("#", "teardown")}"
                val testGroupLabel = "project $projectName"
            }

            listOf(
                    FixtureCountScenario("no_tests", setupFixtureCount = 0, teardownFixtureCount = 0, testLabel = "handle absent # fixture directories"),
                    FixtureCountScenario("setup_teardown", setupFixtureCount = 3, teardownFixtureCount = 2, testLabel = "list the # fixture files"),
                    FixtureCountScenario("setup_teardown_readme", setupFixtureCount = 2, teardownFixtureCount = 3, testLabel = "only include appropriate # files"),
                    FixtureCountScenario("nested", setupFixtureCount = 5, teardownFixtureCount = 5, testLabel = "include nested # directories")
            ).forEach { test ->
                test.apply {
                    describe(testGroupLabel) {
                        val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/$projectName/"))
                        it(setupTestLabel) {
                            expect(setupFixtureCount) { subject.setupFixtures.count() }
                        }

                        it(teardownTestLabel) {
                            expect(teardownFixtureCount) { subject.teardownFixtures.count() }
                        }
                    }
                }
            }

            it("should only include internal hierarchy as the fixture name") {
                val subject = UrlTestProjectResourceLocator(ResourceHelper.getResourceUrl("/projects/nested/"))
                listOf(
                        "bar/set_client_balance",
                        "bar/setup_client",
                        "set_client_balance",
                        "setup_client",
                        "setup_message_log"
                ).forEachIndexed { i, name -> subject.setupFixtures[i].name shouldEqual name }

                listOf(
                        "foo/remove_clients",
                        "foo/remove_transactions",
                        "remove_clients",
                        "remove_some_transactions",
                        "remove_transactions"
                ).forEachIndexed { i, name -> subject.teardownFixtures[i].name shouldEqual name }
            }
        }
    }
})