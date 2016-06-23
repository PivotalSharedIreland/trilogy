package io.pivotal.trilogy.testcase

data class TestArgumentTable(val labels: List<String>, val values: List<List<String>>) {

    val inputArgumentNames: List<String> by lazy {
        labels.filterNot {
            it.isAnOutputArgumentName() or it.isAnErrorLabel()
        }
    }

    val inputArgumentValues: List<List<String>> by lazy {
        valuesAtIndexes(inputArgumentIndexes)
    }

    val outputArgumentNames: List<String> by lazy {
        labels.filter { it.isAnOutputArgumentName() }.map { it.dropLast(1) }
    }

    val outputArgumentValues: List<List<String>> by lazy {
        valuesAtIndexes(outputArgumentIndexes)
    }

    private fun String.isAnOutputArgumentName() = this.endsWith("$")
    private fun String.isAnErrorLabel() = this.toUpperCase().equals("=ERROR=")

    private val inputArgumentIndexes: Set<Int> by lazy {
        inputArgumentNames.map { labels.indexOf(it) }.toSet()
    }

    private val outputArgumentIndexes: Set<Int> by lazy {
        outputArgumentNames.map { labels.indexOf("$it$") }.toSet()
    }

    private fun valuesAtIndexes(indexes: Set<Int>): List<List<String>> {
        return values.map { row ->
            row.filterIndexed { index, s -> indexes.contains(index) }
        }
    }


}
