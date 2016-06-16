package io.pivotal.trilogy.parsing

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TrilogyApplicationOptionsParserTests : Spek({

    it("extracts the test filename") {
        val commandLineArguments = arrayOf("testcase.stt", "--db-url=a")
        val options = TrilogyApplicationOptionsParser.parse(commandLineArguments)
        expect(options.testCaseFilePath) { "testcase.stt" }
    }

    it("extracts the JDBC URL") {
        val jdbcUrl = "jdbc:oracle:thin:@192.168.99.100:32769:xe"
        val commandLineArguments = arrayOf("--db-url=$jdbcUrl", "testcase.stt")
        val options = TrilogyApplicationOptionsParser.parse(commandLineArguments)
        expect(options.jdbcUrl) { jdbcUrl }
    }

})