package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.mocks.TrilogyApplicationOptionsStub
import io.pivotal.trilogy.test_helpers.ResourceHelper
import io.pivotal.trilogy.test_helpers.shouldContain
import io.pivotal.trilogy.test_helpers.shouldNotThrow
import io.pivotal.trilogy.test_helpers.shouldStartWith
import io.pivotal.trilogy.test_helpers.shouldThrow
import io.pivotal.trilogy.testcase.GenericTrilogyTestCase
import io.pivotal.trilogy.testcase.ProcedureTrilogyTestCase
import io.pivotal.trilogy.testcase.TestCaseHooks
import org.amshove.kluent.AnyException
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import kotlin.test.assertTrue
import kotlin.test.expect

class TestProjectBuilderTests : Spek({
    val options = TrilogyApplicationOptionsStub()

    it("should throw exception when building project from an empty directory") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/blank/")
        options.locator = UrlTestProjectResourceLocator(projectUrl);

        { TestProjectBuilder.build(options) } shouldThrow AnyException
    }

    it("should throw an exception when building a project with no tests") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/no_tests/")
        options.locator = UrlTestProjectResourceLocator(projectUrl);

        { TestProjectBuilder.build(options) } shouldThrow AnyException
    }

    context("malformed test cases and tests") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/broken_tests/")

        beforeEach {
            options.locator = UrlTestProjectResourceLocator(projectUrl)
        }

        it("should not throw an exception") {
            { TestProjectBuilder.build(options) } shouldNotThrow AnyException
        }
    }

    context("mixed generic and procedural test cases") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/procedural_and_generic/")

        beforeEach {
            options.locator = UrlTestProjectResourceLocator(projectUrl)
        }

        it("should create the project") {
            { TestProjectBuilder.build(options) } shouldNotThrow AnyException
        }

        it("should read the generic test case") {
            val project = TestProjectBuilder.build(options)
            val testCase = project.testCases[0]

            if (testCase is GenericTrilogyTestCase) {
                expect(1) { testCase.hooks.beforeAll.size }
                expect(1) { testCase.hooks.beforeEachTest.size }
                expect(1) { testCase.hooks.afterAll.size }
                expect(1) { testCase.hooks.afterEachTest.size }
                expect(3) { testCase.tests.size }
                expect("Generic test case example") { testCase.description }
                testCase.tests[0].body shouldContain "RAISE_APPLICATION_ERROR(-20112, "
                testCase.tests[1].body shouldContain "NULL;"
            } else {
                assertTrue(false, "The first test case is expected to be generic")
            }
        }

        it("should read the procedural test case") {
            val project = TestProjectBuilder.build(options)
            val testCase = project.testCases[1]

            if (testCase is ProcedureTrilogyTestCase) {
                expect(1) { testCase.hooks.beforeAll.size }
                expect(0) { testCase.hooks.beforeEachTest.size }
                expect(1) { testCase.hooks.afterAll.size }
                expect(1) { testCase.hooks.afterEachTest.size }
                expect(0) { testCase.hooks.beforeEachRow.size }
                expect(0) { testCase.hooks.afterEachRow.size }
                expect(2) { testCase.tests.size }
                expect("Example") { testCase.description }
            } else {
                assertTrue(false, "The second test case is expected to be procedural")
            }
        }
    }


    it("should create a project with a single test case") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/single_testcase/")
        options.locator = UrlTestProjectResourceLocator(projectUrl)
        val project = TestProjectBuilder.build(options)

        expect(1) { project.testCases.count() }
        (project.testCases.first() as ProcedureTrilogyTestCase).apply {
            procedureName shouldEqual "EXAMPLE_PROCEDURE"
            description shouldEqual "Example"
            hooks shouldEqual TestCaseHooks(emptyList(), emptyList(), emptyList(), emptyList())
            tests.count() shouldEqual 2
            tests.first().apply {
                description shouldEqual "Output should echo the input"
                argumentTable.inputArgumentNames shouldEqual listOf("V_IN")
                argumentTable.outputArgumentNames shouldEqual listOf("V_OUT", "=ERROR=")
                argumentTable.inputArgumentValues.count() shouldEqual 3
                argumentTable.inputArgumentValues.first() shouldEqual listOf("1243")
                argumentTable.outputArgumentValues.first() shouldEqual listOf("1243", "")
            }
        }

        expect(0) { project.fixtures.setupFixtureCount }
        expect(0) { project.fixtures.teardownFixtureCount }
        expect(emptyList()) { project.sourceScripts }
        expect(null) { project.schema }
    }

    it("should create a project with source scripts") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/simple/")
        options.locator = UrlTestProjectResourceLocator(projectUrl)
        val project = TestProjectBuilder.build(options)

        expect(2) { project.sourceScripts.count() }
        project.sourceScripts[0].content shouldStartWith "CREATE OR REPLACE PROCEDURE EXAMPLE$"
        project.sourceScripts[1].content shouldStartWith "CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE"
    }

    it("should create a project with a schema") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/schema/")
        options.locator = UrlTestProjectResourceLocator(projectUrl)
        val project = TestProjectBuilder.build(options)

        project.schema!!.shouldStartWith("CREATE TABLE CLIENTS")
    }

    it("should create a project with fixtures") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/setup_teardown/")
        options.locator = UrlTestProjectResourceLocator(projectUrl)
        val project = TestProjectBuilder.build(options)

        val fixtures = project.fixtures
        fixtures.getSetupFixtureByName("Reset client balance") shouldStartWith "UPDATE CLIENTS SET BALANCE=0 WHERE ID=66778899"
        fixtures.getTeardownFixtureByName("Remove transactions") shouldStartWith "DELETE FROM TRANSACTIONS"
    }

    it("should omit the schema when the options flag is set") {
        val projectUrl = ResourceHelper.getResourceUrl("/projects/schema/")
        val schemalessOptions = TrilogyApplicationOptionsStub(shouldSkipSchema = true)
                .apply { locator = UrlTestProjectResourceLocator(projectUrl) }
        val project = TestProjectBuilder.build(schemalessOptions)

        expect(null) { project.schema }

    }
})

