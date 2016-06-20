package io.pivotal.trilogy.application

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TrilogyApplicationTests : Spek({

    describe("execution") {
        val jdbcUrl = "jdbc:oracle:thin:@192.168.99.100:32769:xe"

        describe("simple cases") {
            it("succeeds for a simple case") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/should_pass.stt", jdbcUrl)
                expect(true) { TrilogyApplication().run(options) }
            }

            it("fails for a simple case") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/should_fail.stt", jdbcUrl)
                expect(false) { TrilogyApplication().run(options) }
            }
        }

        describe("tests with assertions") {
            it("succeed when the assertions pass") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/should_pass_with_sql.stt", jdbcUrl)
                expect(true) { TrilogyApplication().run(options) }
            }

            it("fail when the assertions raise an error") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/should_fail_with_sql.stt", jdbcUrl)
                expect(false) { TrilogyApplication().run(options) }
            }
        }

        describe("multiple tests in a test case") {
            it("succeeds when all the tests succeed") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/multiple/shouldPass.stt", jdbcUrl)
                expect(true) { TrilogyApplication().run(options) }
            }

            it("fails when one of the tests is failing") {
                val options = TrilogyApplicationOptions("src/test/resources/testcases/multiple/shouldFail.stt", jdbcUrl)
                expect(false) { TrilogyApplication().run(options) }
            }
        }
    }

})
