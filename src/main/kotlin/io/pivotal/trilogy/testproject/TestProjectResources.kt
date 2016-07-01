package io.pivotal.trilogy.testproject

import java.io.File
import java.net.URL

data class TestProjectResources(val projectUrl: URL) {
    val testsDirectory: File by lazy { File("${projectUrl.path}tests") }
    val sourceDirectory: File by lazy { File("${projectUrl.path}src") }
    val schemaFile: File by lazy { File("${projectUrl.path}tests/fixtures/schema.sql") }
    val testsAbsent: Boolean by lazy { !testsPresent }
    val testsPresent: Boolean by lazy {
        testsDirectory.isDirectory && testsDirectory.listFiles().any { file -> file.name.endsWith(".stt") }
    }
}