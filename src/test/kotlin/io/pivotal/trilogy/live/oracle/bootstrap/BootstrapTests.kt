package io.pivotal.trilogy.live.oracle.bootstrap

import io.pivotal.trilogy.test_helpers.DatabaseHelper
import org.jetbrains.spek.api.Spek

class BootstrapTests : Spek({
    DatabaseHelper.executeScript("degenerate")
    DatabaseHelper.executeScript("allInParamTypes")
    it("loads") {}
})