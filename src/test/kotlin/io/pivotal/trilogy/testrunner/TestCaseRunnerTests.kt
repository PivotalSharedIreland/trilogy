package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.DatabaseHelper
import io.pivotal.trilogy.Fixtures
import org.jetbrains.spek.api.Spek
import kotlin.test.expect


class TestCaseRunnerTests : Spek({

    val testCaseRunner = TestCaseRunner(DatabaseHelper.jdbcUrl)

    it("should run successful test case") {
        val trilogyTestCase = Fixtures.getTestCase("should_pass")

        expect(true) { testCaseRunner.run(trilogyTestCase).didPass }
    }

    it("should run the failing test case") {
        val trilogyTestCase = Fixtures.getTestCase("should_fail")

        expect(false) { testCaseRunner.run(trilogyTestCase).didPass }
    }

    it("should run successful test case with a SQL assertion") {
        val trilogyTestCase = Fixtures.getTestCase("should_pass_with_sql")

        expect(true) { testCaseRunner.run(trilogyTestCase).didPass }
    }

    it("should run failing test case with a SQL assertion") {
        val trilogyTestCase = Fixtures.getTestCase("should_fail_with_sql")

        expect(false) { testCaseRunner.run(trilogyTestCase).didPass }
    }

    it("should run test case with two tests") {
        val trilogyTestCase = Fixtures.getTestCase("multiple/shouldPass")

        expect(true) { testCaseRunner.run(trilogyTestCase).didPass }
    }

    context("test case with one passing and one failing test") {
        val trilogyTestCase = Fixtures.getTestCase("multiple/shouldFail")

        it("should run test case with two tests with one failing") {
            expect(false) { testCaseRunner.run(trilogyTestCase).didPass }
        }

        it("should report the number of failing tests") {
            expect(1) { testCaseRunner.run(trilogyTestCase).failed }
        }

        it("should report the number of passing tests") {
            expect(1) { testCaseRunner.run(trilogyTestCase).passed }
        }
    }

})

