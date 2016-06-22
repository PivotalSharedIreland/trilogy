package io.pivotal.trilogy.parsing

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TrilogyApplicationOptionsParserTests : Spek({

    it("extracts the test filename") {
        val commandLineArguments = arrayOf("testcase.stt")
        val options = TrilogyApplicationOptionsParser.parse(commandLineArguments)
        expect(options.testCaseFilePath) { "testcase.stt" }
    }

})
