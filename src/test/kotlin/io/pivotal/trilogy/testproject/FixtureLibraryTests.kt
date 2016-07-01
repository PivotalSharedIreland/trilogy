package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.shouldNotThrow
import io.pivotal.trilogy.shouldThrow
import org.amshove.kluent.AnyException
import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class FixtureLibraryTests : Spek({
    it("finds a before fixture by name") {
        val fixtureText = "Foo bar"
        val subject = FixtureLibrary(mapOf(Pair("setup/fishs_are_the_planks_of_the_golden_urchin", fixtureText)))
        expect(fixtureText) { subject.getSetupFixtureByName("Fishs are the planks of the golden urchin") }
    }

    it("finds a after fixture by name") {
        val fixtureText = "Bar baz"
        val subject = FixtureLibrary(mapOf(Pair("teardown/i_beam_this_beauty_its_called_senior_mankind", fixtureText)))
        expect (fixtureText) { subject.getTeardownFixtureByName("I beam this beauty its called senior mankind") }
    }

    it("finds a before fixture inside a folder") {
        val fixtureText = "Everyone just loves the sourness of onion platter soakd with woodruff."
        val subject = FixtureLibrary(mapOf(Pair("setup/how_big/a_pure_form_of_purpose_is_the_milk", fixtureText)))
        expect(fixtureText) { subject.getSetupFixtureByName("hOw BiG  /  A pure form of purpose is the milk") }
    }

    it("crashes when accessing non-existing fixture") {
        val emptyLibrary = FixtureLibrary.emptyLibrary()
        val getFixtureByNameBlock = { emptyLibrary.getTeardownFixtureByName("aasdfad") }

        getFixtureByNameBlock shouldThrow AnyException
    }

    it("is able to provide an null object") {
        { FixtureLibrary.emptyLibrary() } shouldNotThrow AnyException
        expect(emptyMap()) { FixtureLibrary.emptyLibrary().fixtures }
    }
})