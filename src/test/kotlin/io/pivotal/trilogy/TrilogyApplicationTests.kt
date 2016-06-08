package io.pivotal.trilogy

import io.pivotal.trilogy.application.TrilogyApplication
import io.pivotal.trilogy.application.TrilogyApplicationOptions
import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TrilogyApplicationTests : Spek({
    describe("execution") {
        it("succeeds for a simple case") {
            val options = TrilogyApplicationOptions("should_pass.stt", "")
            expect(TrilogyApplication().run(options)) { true }
        }

        it("fails for a simple case") {
            val options = TrilogyApplicationOptions("should_fail.stt", "")
            expect(TrilogyApplication().run(options)) { false }
        }
    }
});
