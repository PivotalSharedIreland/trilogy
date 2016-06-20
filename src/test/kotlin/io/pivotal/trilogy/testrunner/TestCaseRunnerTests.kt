package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.DatabaseHelper
import io.pivotal.trilogy.Fixtures
import org.jetbrains.spek.api.Spek
import kotlin.test.expect


class TestCaseRunnerTests : Spek({

    it("should run successful test case") {
        val trilogyTestCase = Fixtures.getTestCase("should_pass")

        expect(true) { TestCaseRunner(DatabaseHelper.jdbcUrl).run(trilogyTestCase) }
    }

    it("should run the failing test case") {
        val trilogyTestCase = Fixtures.getTestCase("should_fail")

        expect(false) { TestCaseRunner(DatabaseHelper.jdbcUrl).run(trilogyTestCase) }
    }

    it("should run successful test case with a SQL assertion") {
        val trilogyTestCase = Fixtures.getTestCase("should_pass_with_sql")

        expect(true) { TestCaseRunner(DatabaseHelper.jdbcUrl).run(trilogyTestCase) }
    }

    it("should run failing test case with a SQL assertion") {
        val trilogyTestCase = Fixtures.getTestCase("should_fail_with_sql")

        expect(false) { TestCaseRunner(DatabaseHelper.jdbcUrl).run(trilogyTestCase) }
    }

    it("should run test case with two tests") {
        val trilogyTestCase = Fixtures.getTestCase("multiple/shouldPass")

        expect(true) { TestCaseRunner(DatabaseHelper.jdbcUrl).run(trilogyTestCase) }
    }

    it("should run test case with two tests with one failing") {
        val trilogyTestCase = Fixtures.getTestCase("multiple/shouldFail")

        expect(false) { TestCaseRunner(DatabaseHelper.jdbcUrl).run(trilogyTestCase) }
    }
})

