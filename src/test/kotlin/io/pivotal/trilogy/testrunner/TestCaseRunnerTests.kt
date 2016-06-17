package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.Fixtures
import org.jetbrains.spek.api.Spek
import kotlin.test.expect


class TestCaseRunnerTests : Spek({
    // TODO: externalize the jdbc URL
    val jdbcUrl = "jdbc:oracle:thin:@192.168.99.100:32769:xe"

    it("should run successful test case") {
        val trilogyTestCase = Fixtures.getTestCase("should_pass")

        expect(true) { TestCaseRunner(trilogyTestCase, jdbcUrl).run() }
    }

    it("should run the failing test case") {
        val trilogyTestCase = Fixtures.getTestCase("should_fail")

        expect(false) { TestCaseRunner(trilogyTestCase, jdbcUrl).run() }
    }

    it("should run successful test case with a SQL assertion") {
        val trilogyTestCase = Fixtures.getTestCase("should_pass_with_sql")

        expect(true) { TestCaseRunner(trilogyTestCase, jdbcUrl).run() }
    }

    it("should run failing test case with a SQL assertion") {
        val trilogyTestCase = Fixtures.getTestCase("should_fail_with_sql")

        expect(false) { TestCaseRunner(trilogyTestCase, jdbcUrl).run() }
    }
})

