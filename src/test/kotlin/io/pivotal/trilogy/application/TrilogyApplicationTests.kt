package io.pivotal.trilogy.application

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TrilogyApplicationTests : Spek({

    describe("execution") {
        val jdbcUrl = "jdbc:oracle:thin:@192.168.99.100:32769:xe"

        it("succeeds for a simple case") {
            val options = TrilogyApplicationOptions("src/test/resources/testcases/should_pass.stt", jdbcUrl)
            expect(TrilogyApplication().run(options)) { true }
        }

        it("fails for a simple case") {
            val options = TrilogyApplicationOptions("src/test/resources/testcases/should_fail.stt", jdbcUrl)
            expect(TrilogyApplication().run(options)) { false }
        }
    }

})
