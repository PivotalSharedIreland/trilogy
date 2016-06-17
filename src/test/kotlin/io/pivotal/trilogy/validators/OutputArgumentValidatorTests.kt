package io.pivotal.trilogy.validators

import org.jetbrains.spek.api.Spek
import java.math.BigDecimal
import kotlin.test.expect

class OutputArgumentValidatorTests : Spek ({

    it("passes validation with no parameters") {
        expect(true) { OutputArgumentValidator(emptyList()).validate(emptyList(), emptyMap()) }
    }

    it("passes validation with one parameter") {
        expect(true) { OutputArgumentValidator(listOf("FOO")).validate(listOf("1"), mapOf(Pair("FOO", BigDecimal.ONE))) }
    }

    it("fails validation with one parameter") {
        expect(false) { OutputArgumentValidator(listOf("FOO")).validate(listOf("1"), mapOf(Pair("FOO", BigDecimal.TEN))) }
    }

    it("passes validation with multiple parameters") {
        val actualValues = mapOf(Pair("FOO", BigDecimal.ONE), Pair("BAR", BigDecimal.TEN))
        expect(true) { OutputArgumentValidator(listOf("FOO", "BAR")).validate(listOf("1", "10"), actualValues) }
    }

    it("fails validation with multiple parameters") {
        val actualValues = mapOf(Pair("FOO", BigDecimal.ONE), Pair("BAR", BigDecimal.TEN))
        expect(false) { OutputArgumentValidator(listOf("FOO", "BAR")).validate(listOf("10", "1"), actualValues) }
    }

    it("passes validation with a null value") {
        val actualValues = mapOf(Pair("FOO", null), Pair("BAR", BigDecimal.TEN))
        expect(true) { OutputArgumentValidator(listOf("FOO", "BAR")).validate(listOf("__NULL__", "10"), actualValues) }
    }

})