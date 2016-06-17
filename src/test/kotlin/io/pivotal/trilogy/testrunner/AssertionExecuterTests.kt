package io.pivotal.trilogy.testrunner

import io.pivotal.trilogy.testcase.TrilogyAssertion
import org.jetbrains.spek.api.Spek
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource
import kotlin.test.expect

class AssertionExecuterTests : Spek ({
    describe("executing assertions") {
        it("returns true when the assertion does not raise an error") {
            val executer = AssertionExecuter(dataSource())
            val sql = "BEGIN NULL; END;"
            expect(true) { executer.execute(TrilogyAssertion("", sql)) }
        }

        it("returns false when the assertion raises an error") {
            val executer = AssertionExecuter(dataSource())
            val sql = "BEGIN RAISE_APPLICATION_ERROR(-20000, 'Oops'); END;"
            expect(false) { executer.execute(TrilogyAssertion("", sql)) }
        }

    }
})

fun dataSource(): DataSource {
    return DriverManagerDataSource().apply {
        setDriverClassName("oracle.jdbc.driver.OracleDriver")
        url = "jdbc:oracle:thin:@192.168.99.100:32769:xe"
        username = "APP_USER"
        password = "secret"
    }

}