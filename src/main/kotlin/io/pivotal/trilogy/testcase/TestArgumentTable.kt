package io.pivotal.trilogy.testcase

data class TestArgumentTable(private val labels: List<String>, private val values: List<List<String>>) {

    val inputArgumentNames: List<String> by lazy {
        labels.filterNot { it.isValidationLabel() }
    }

    val inputArgumentValues: List<List<String>> by lazy {
        valuesAtIndexes(inputArgumentIndexes)
    }

    val outputArgumentNames: List<String> by lazy {
        labels.filter { it.isValidationLabel() }.map { name -> name.toValidationLabel() }
    }

    val outputArgumentValues: List<List<String>> by lazy {
        valuesAtIndexes(outputArgumentIndexes)
    }

    private fun String.isValidationLabel() = this.isAnErrorLabel() || this.isAnOutputArgumentName()
    private fun String.isAnOutputArgumentName() = this.endsWith("$")
    private fun String.isAnErrorLabel() = this.toUpperCase().equals(TestArgumentTableTokens.errorColumnName)
    private fun String.toValidationLabel() = if (this.isAnErrorLabel()) this else this.dropLast(1)
    private fun String.labelIndex() = if (this.isAnErrorLabel()) labels.indexOf(this) else labels.indexOf("$this$")

    private val inputArgumentIndexes: Set<Int> by lazy {
        inputArgumentNames.map { labels.indexOf(it) }.toSet()
    }

    private val outputArgumentIndexes: Set<Int> by lazy {
        outputArgumentNames.map { name -> name.labelIndex() }.toSet()
    }

    private fun valuesAtIndexes(indexes: Set<Int>): List<List<String>> {
        return values.map { row ->
            row.filterIndexed { index, s -> indexes.contains(index) }
        }
    }


}
