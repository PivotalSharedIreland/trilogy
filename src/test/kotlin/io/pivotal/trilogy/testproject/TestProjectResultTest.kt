package io.pivotal.trilogy.testproject

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TestProjectResultTest : Spek({
    it("is not failed by default") {
        expect(false) { TestProjectResult(emptyList()).didFail }
    }

    it("has failed when a failure message is present") {
        expect(true) { TestProjectResult(emptyList(), "message").didFail }
    }
})