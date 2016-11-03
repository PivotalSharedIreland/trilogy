package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.testcase.TrilogyTest

class StringTestParser(val testBody: String) : TestParser {
    class InvalidTestFormat(message: String?) : RuntimeException(message)
    class MissingDescription(message: String?) : RuntimeException(message)

    private val parser: TestParser by lazy {
        try {
            ProcedureStringTestParser(testBody)
        } catch(e: RuntimeException) {
            GenericStringTestParser(testBody)
        }
    }


    override fun getTest(): TrilogyTest {
        return parser.getTest()
    }

}