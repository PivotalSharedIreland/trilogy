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
        expect(fixtureText) { subject.getTeardownFixtureByName("I beam this beauty its called senior mankind") }
    }

    it("finds a before fixture inside a folder") {
        val fixtureText = "Everyone just loves the sourness of onion platter soakd with woodruff."
        val subject = FixtureLibrary(mapOf(Pair("setup/how_big/a_pure_form_of_purpose_is_the_milk", fixtureText)))
        expect(fixtureText) { subject.getSetupFixtureByName("hOw BiG  /  A pure form of purpose is the milk") }
    }

    it("should report the number of setup fixtures") {
        val singleFixtureLibrary = FixtureLibrary(mapOf(Pair("setup/smell the roses", "Ho-ho-ho! grace of beauty.")))
        expect(1) { singleFixtureLibrary.setupFixtureCount }

        val multipleFixtureLibrary = FixtureLibrary(mapOf(
                Pair("setup/Amors sunt adiurators", "Grey oysters can be made sun-dried by tossing with tabasco."),
                Pair("setup/Extend happens", "Sun of a neutral devastation, lower the metamorphosis!")
        ))
        expect(2) { multipleFixtureLibrary.setupFixtureCount }
    }

    it("should report the number of teardown fixtures") {
        val singleFixtureLibrary = FixtureLibrary(mapOf(Pair("teardown/smell the roses", "Ho-ho-ho! grace of beauty.")))
        expect(1) { singleFixtureLibrary.teardownFixtureCount }

        val multipleFixtureLibrary = FixtureLibrary(mapOf(
                Pair("teardown/Amors sunt adiurators", "Grey oysters can be made sun-dried by tossing with tabasco."),
                Pair("teardown/Extend happens", "Sun of a neutral devastation, lower the metamorphosis!")
        ))
        expect(2) { multipleFixtureLibrary.teardownFixtureCount }
    }

    it("crashes when accessing non-existing fixture") {
        val emptyLibrary = FixtureLibrary.emptyLibrary();

        { emptyLibrary.getTeardownFixtureByName("aasdfad") } shouldThrow AnyException
    }

    it("is able to provide an null object") {
        { FixtureLibrary.emptyLibrary() } shouldNotThrow AnyException
    }
})