package io.pivotal.trilogy.testproject


data class FixtureLibrary(val fixtures: Map<String, String>) {

    companion object {
        fun emptyLibrary() : FixtureLibrary {
            return FixtureLibrary(emptyMap())
        }
    }

    fun getSetupFixtureByName(name: String): String {
        return fixtures[beforeFixtureKey(name)]!!
    }

    fun getTeardownFixtureByName(name: String): String {
        return fixtures[afterFixtureKey(name)]!!
    }

    private fun afterFixtureKey(name: String): String {
        return "teardown/${fixtureKey(name)}"
    }

    private fun beforeFixtureKey(name: String): String {
        return "setup/${fixtureKey(name)}"
    }

    private fun fixtureKey(name: String): String {
        return name.toLowerCase().replace(Regex("\\s*/\\s*"), "/").replace(" ", "_")
    }


}