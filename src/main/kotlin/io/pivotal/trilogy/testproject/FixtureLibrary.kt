package io.pivotal.trilogy.testproject


class FixtureLibrary(private val fixtures: Map<String, String>) {

    companion object {
        fun emptyLibrary(): FixtureLibrary {
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

    val setupFixtureCount: Int by lazy {
        fixtures.keys.fold(0) { count, name ->
            if (name.startsWith("setup/")) count + 1 else count
        }
    }
    val teardownFixtureCount: Int by lazy {
        fixtures.keys.fold(0) { count, name ->
            if (name.startsWith("teardown/")) count + 1 else count
        }
    }


}