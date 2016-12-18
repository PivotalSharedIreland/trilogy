package io.pivotal.trilogy.validators

import org.jetbrains.spek.api.Spek
import java.math.BigDecimal
import kotlin.test.expect

class OutputArgumentValidatorTests : Spek({

    it("passes validation with no parameters") {
        expect(null) { OutputArgumentValidator(emptyList()).validate(emptyList(), emptyMap()) }
    }

    it("passes validation with one parameter") {
        expect(null) { OutputArgumentValidator(listOf("FOO")).validate(listOf("1"), mapOf("FOO" to BigDecimal.ONE)) }
    }

    it("fails validation with one parameter") {
        expect("value mismatch") { OutputArgumentValidator(listOf("FOO")).validate(listOf("1"), mapOf("FOO" to BigDecimal.TEN)) }
    }

    it("passes validation with multiple parameters") {
        val actualValues = mapOf(Pair("FOO", BigDecimal.ONE), Pair("BAR", BigDecimal.TEN))
        expect(null) { OutputArgumentValidator(listOf("FOO", "BAR")).validate(listOf("1", "10"), actualValues) }
    }

    it("fails validation with multiple parameters") {
        val actualValues = mapOf(Pair("FOO", BigDecimal.ONE), Pair("BAR", BigDecimal.TEN))
        expect("value mismatch") { OutputArgumentValidator(listOf("FOO", "BAR")).validate(listOf("10", "1"), actualValues) }
    }

    it("passes validation with a null value") {
        val actualValues = mapOf(Pair("FOO", null), Pair("BAR", BigDecimal.TEN))
        expect(null) { OutputArgumentValidator(listOf("FOO", "BAR")).validate(listOf("__NULL__", "10"), actualValues) }
    }

    context("Error validation") {
        val subject = OutputArgumentValidator(listOf("=ERROR="))
        val actualValues = mapOf("=ERROR=" to "ORA-06512: at line 1")

        it("passes validation when no error is raised") {
            expect(null) { subject.validate(listOf(""), emptyMap()) }
        }

        it("fails validation when an unexpected error is raised") {
            expect("unexpected error") { subject.validate(listOf(""), mapOf("=ERROR=" to "Something")) }
        }

        it("passes validation for error wildcard") {
            expect(null) { subject.validate(listOf("ANY"), actualValues) }
        }

        it("fails if an error is expected but not thrown") {
            expect("Expected any error to occur, but no errors were triggered") { subject.validate(listOf("any"), emptyMap()) }
        }

        it("fails when a different error is expected") {
            expect("Expected an error with text 'Bunny' to occur, but the error was:\nORA-06512: at line 1") { subject.validate(listOf("Bunny"), actualValues) }
        }

        it("fails when a specific error is expected, but no error is thrown") {
            expect("Expected an error with text 'FOO' to occur, but no errors were triggered") { subject.validate(listOf("FOO"), emptyMap()) }
        }

        context("with output arguments") {
            val subjectWithOutput = OutputArgumentValidator(listOf("V_OUT", "=ERROR="))

            it("ignores the output values when an error is thrown") {
                expect(null) { subjectWithOutput.validate(listOf("Jam", "ANY"), mapOf("=ERROR=" to "Ouch!")) }
            }
        }
    }


})