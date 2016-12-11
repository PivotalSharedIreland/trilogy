package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.test_helpers.ResourceHelper
import io.pivotal.trilogy.test_helpers.shouldContain
import io.pivotal.trilogy.test_helpers.shouldThrow
import org.amshove.kluent.AnyException
import org.jetbrains.spek.api.Spek
import kotlin.test.expect


class GenericStringTestParserTests : Spek({
    context("minimal") {
        val testString = ResourceHelper.getTestByName("genericMinimal")

        it("can be read") {
            GenericStringTestParser(testString).getTest()
        }

        it("reads the test description") {
            expect("Test description") { GenericStringTestParser(testString).getTest().description }
        }

        it("reads the test") {
            expect("BEGIN\n  NULL;\nEND;") { GenericStringTestParser(testString).getTest().body }
        }

        it("reads the assertions") {
            expect(0) { GenericStringTestParser(testString).getTest().assertions.size }
        }

    }

    context("with assertions") {
        val testString = ResourceHelper.getTestByName("genericWithAssertions")

        it("reads 2 assertions") {
            expect(2) { GenericStringTestParser(testString).getTest().assertions.size }
        }

        it("reads assertion descriptions") {
            expect("Assertion description 1") { GenericStringTestParser(testString).getTest().assertions[0].description }
            expect("Assertion description 2") { GenericStringTestParser(testString).getTest().assertions[1].description }
        }

        it("reads assertion bodies") {
            GenericStringTestParser(testString).getTest().assertions[0].body shouldContain "l_count NUMBER"
            GenericStringTestParser(testString).getTest().assertions[1].body shouldContain "alt_count NUMBER"
        }
    }

    it("requires a test body") {
        { GenericStringTestParser("## TEST\nStigma at the alpha quadrant") } shouldThrow GenericStringTestParser.MissingTestBody::class
    }

    it("cannot contain a data section") {
        { GenericStringTestParser("## TEST\nBlah\n### DATA\n| P1 |\n|----|\n| 12 |\n") } shouldThrow AnyException
    }
})