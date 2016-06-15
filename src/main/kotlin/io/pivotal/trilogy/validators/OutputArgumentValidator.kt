package io.pivotal.trilogy.validators

class OutputArgumentValidator(val parameterNames: List<String>) {

    fun validate(expectedRow: List<String>, actualValues: Map<String, Any>): Boolean {
        if (parameterNames.isEmpty()) return true

        return expectedMap(expectedRow).equals(actualMap(actualValues))
    }

    private fun actualMap(actualValues: Map<String, Any>): Map<String, String> {
        return actualValues.mapValues { "${it.value}" }
    }

    private fun expectedMap(expectedRow: List<String>) = parameterNames.zip(expectedRow).toMap()

}