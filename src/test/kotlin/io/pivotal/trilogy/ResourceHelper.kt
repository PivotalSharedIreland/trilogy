package io.pivotal.trilogy

class ResourceHelper {
    companion object {
        fun getResourceAsText(name: String): String {
            return ResourceHelper::class.java
                    .getResourceAsStream(name)
                    .reader().readText()
        }

        fun getTestCaseByName(name: String): String {
            return getResourceAsText("/testcases/$name.stt")
        }

        fun getTestByName(name: String) : String {
            return getResourceAsText("/tests/$name.md")
        }
    }
}