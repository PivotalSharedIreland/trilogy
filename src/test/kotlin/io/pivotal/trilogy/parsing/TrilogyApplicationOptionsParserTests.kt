package io.pivotal.trilogy.parsing

import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class TrilogyApplicationOptionsParserTests : Spek({

    it("extracts the test filename") {
        val commandLineArguments = arrayOf("testcase.stt")
        val options = TrilogyApplicationOptionsParser.parse(commandLineArguments)
        expect("testcase.stt") { options.testCaseFilePath }
    }

    it("extracts the project path") {
        val pathToProject = "/Oysters/combines/greatly/with/quartered/pickles"
        val commandLineArguments = arrayOf("--project=$pathToProject")
        val options = TrilogyApplicationOptionsParser.parse(commandLineArguments)
        expect(pathToProject) { options.testProjectPath }
        expect(null) { options.testCaseFilePath }
    }

    it("project path to empty string when no options are specified") {
        val options = TrilogyApplicationOptionsParser.parse(emptyArray())
        expect(null) { options.testCaseFilePath }
        expect("") { options.testProjectPath }
    }

    it("only processes the application-related arguments") {
        val options = TrilogyApplicationOptionsParser.parse(arrayOf("--db_url=funny_bunny", "--project=project", "--foo=bar"))
        expect("project") { options.testProjectPath }
    }

    it("sets the skip schema load to false by default") {
        val options = TrilogyApplicationOptionsParser.parse(arrayOf("--db_url=funny_bunny", "--project=project", "--foo=bar"))
        expect(false) { options.shouldSkipSchema }
    }

    it("fetches the skip schema load option") {
        val options = TrilogyApplicationOptionsParser.parse(arrayOf("--skip_schema_load"))
        expect(true) { options.shouldSkipSchema }
    }

})