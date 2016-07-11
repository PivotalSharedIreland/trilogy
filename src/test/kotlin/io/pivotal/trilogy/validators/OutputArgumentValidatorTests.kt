package io.pivotal.trilogy.validators

import org.jetbrains.spek.api.Spek
import java.math.BigDecimal
import kotlin.test.expect

class OutputArgumentValidatorTests : Spek({

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

    context("Error validation") {
        val subject = OutputArgumentValidator(listOf("=ERROR="))
        val actualValues = mapOf(Pair("=ERROR=", "ORA-06512: at line 1"))

        it("passes validation when no error is raised") {
            expect(true) { subject.validate(listOf(""), emptyMap()) }
        }

        it("fails validation when an unexpected error is raised") {
            expect(false) { subject.validate(listOf(""), mapOf(Pair("=ERROR=", "Something"))) }
        }

        it("passes validation for error wildcard") {
            expect(true) { subject.validate(listOf("ANY"), actualValues) }
        }

        it("fails if an error is expected but not thrown") {
            expect(false) { subject.validate(listOf("any"), emptyMap()) }
        }

        it("fails when a different error is expected") {
            expect(false) { subject.validate(listOf("Bunny"), actualValues) }
        }

        context("with output arguments") {
            val subjectWithOutput = OutputArgumentValidator(listOf("V_OUT", "=ERROR="))

            it("ignores the output values when an error is thrown") {
                expect(true) { subjectWithOutput.validate(listOf("Jam", "ANY"), mapOf(Pair("=ERROR=", "Ouch!"))) }
            }
        }
    }


})