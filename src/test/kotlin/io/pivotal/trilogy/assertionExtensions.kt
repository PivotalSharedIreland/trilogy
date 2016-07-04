package io.pivotal.trilogy

import org.amshove.kluent.`should not throw`
import org.amshove.kluent.`should throw`
import kotlin.reflect.KClass
import kotlin.test.assertTrue

infix fun String.shouldContain(other: String) = assertTrue(this.contains(other), "Expected '$this' to contain '$other'")
infix fun String.shouldStartWith(other: String) = assertTrue(this.startsWith(other), "Expected '$this' to start with '$other'")
infix fun String.shouldEndWith(other: String) = assertTrue(this.endsWith(other), "Expected '$this' to end with '$other'")

infix fun <T : Exception> (() -> Any).shouldThrow(expectedException: KClass<T>) {
    val voidBlock = { this.invoke(); Unit }
    voidBlock `should throw` expectedException
}

infix fun <T : Exception> (() -> Any).shouldNotThrow(expectedException: KClass<T>) {
    val voidBlock = { this.invoke(); Unit }
    voidBlock `should not throw` expectedException
}
