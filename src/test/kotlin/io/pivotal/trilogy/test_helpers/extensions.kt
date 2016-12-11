package io.pivotal.trilogy.test_helpers

fun <T> Int.timesRepeat(block: () -> T): List<T> = (0 until this).map { block() }