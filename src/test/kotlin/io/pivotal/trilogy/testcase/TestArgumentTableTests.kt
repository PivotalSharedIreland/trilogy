package io.pivotal.trilogy.testcase

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TestArgumentTableTests : Spek ({

    val labels = listOf("FOO", "BAR$", "=ERROR=")

    it("produces input argument names") {
        expect (listOf("FOO", "BAR")) { TestArgumentTable(listOf("FOO", "BAR"), emptyList()).inputArgumentNames }
    }

    it("produces input argument values") {
        val argumentValues = listOf(listOf("v1", "v2"), listOf("v3", "v4"))
        expect(argumentValues) { TestArgumentTable(listOf("arg1", "arg2"), argumentValues).inputArgumentValues }
    }

    it("excludes non-input argument names") {
        expect (listOf("FOO")) { TestArgumentTable(labels, emptyList()).inputArgumentNames }
    }

    it("excludes non-input argument values") {
        val tableValues = listOf(listOf("1", "1", ""), listOf("0", "0", ""))
        expect(listOf(listOf("1"), listOf("0"))) { TestArgumentTable(labels, tableValues).inputArgumentValues }
    }

    it("produces output argument names") {
        expect(listOf("FOO", "BAR")) { TestArgumentTable(listOf("FOO$", "BAR$"), emptyList()).outputArgumentNames }
    }

    it("excludes input argument names and the error label") {
        expect(listOf("FOO", "BAR")) { TestArgumentTable(listOf("FOO$", "BAR$", "BAZ", "=ERROR="), emptyList()).outputArgumentNames }
    }

    it("produces a list of matching output values") {
        val outputValues = listOf(listOf("1", "2", "3", ""), listOf("5", "6", "7", ""))
        expect(listOf(listOf("2", "3"), listOf("6", "7"))) { TestArgumentTable(listOf("ANIM", "FOO$", "BAR$", "=ERROR="), outputValues).outputArgumentValues}
    }
})
