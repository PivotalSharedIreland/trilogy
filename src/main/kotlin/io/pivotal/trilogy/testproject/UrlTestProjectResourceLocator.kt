package io.pivotal.trilogy.testproject

import java.io.File
import java.io.FileFilter
import java.net.URL

class UrlTestProjectResourceLocator(val projectUrl: URL) : TestProjectResourceLocator {
    override val testsPresent: Boolean by lazy {
        testsDirectory.isDirectory && testsDirectory.listFiles(testFileFilter).any()
    }

    override val schema: String? by lazy {
        if (schemaFile.isFile) schemaFile.readText() else null
    }

    override val testCases: List<String> by lazy {
        testCaseFiles.map { testCaseFile -> testCaseFile.readText() }
    }

    override val sourceScripts: List<ProjectSourceScript> by lazy {
        sourceFiles.map { sourceFile -> ProjectSourceScript(name = sourceFile.absolutePath, content = sourceFile.readText()) }
    }

    override val setupFixtures: List<NamedStringResource> by lazy {
        setupFixtureFiles.map { file -> NamedStringResource(file.fixtureName!!, file.readText()) }
    }

    override val teardownFixtures: List<NamedStringResource> by lazy {
        teardownFixtureFiles.map { file -> NamedStringResource(file.fixtureName!!, file.readText()) }
    }

    private val File.fixtureName: String? get() {
        return Regex("$fixturesPath(setup|teardown)[\\\\/](.+)\\.sql").find(this.absolutePath).let { match ->
            match?.groupValues?.get(2)?.replace(Regex("\\s*([\\\\/])\\s*"), "$1")
        }
    }

    private val testCaseFiles: List<File> by lazy {
        testsDirectory.listFilesRecursively(testFileFilter)
    }
    private val schemaFile: File by lazy { File("${fixturesPath}schema.sql") }
    private val sourceFiles: List<File> by lazy {
        sourceDirectory.listFilesRecursively(sourceFileFilter)
    }
    private val setupFixtureFiles: List<File> by lazy {
        if (setupFixturesDirectory.isDirectory)
            setupFixturesDirectory.listFilesRecursively(fixtureFileFilter)
        else
            emptyList()
    }
    private val teardownFixtureFiles: List<File> by lazy {
        if (teardownFixturesDirectory.isDirectory)
            teardownFixturesDirectory.listFilesRecursively(fixtureFileFilter)
        else
            emptyList()
    }



    private val testsDirectory: File by lazy { File("${projectUrl.path}tests") }
    private val sourceDirectory: File by lazy { File("${projectUrl.path}src") }
    private val setupFixturesDirectory: File by lazy { File(setupFixturesPath) }
    private val teardownFixturesDirectory: File by lazy { File(teardownFixturesPath) }


    private val testFileFilter = fileFilterByExtension("stt")
    private val sourceFileFilter = fileFilterByExtension("sql")
    private val fixtureFileFilter = sourceFileFilter
    private val fixturesPath: String by lazy { "${projectUrl.path}tests/fixtures/" }
    private val setupFixturesPath: String by lazy { "${fixturesPath}setup/" }
    private val teardownFixturesPath: String by lazy { "${fixturesPath}teardown/" }
    private fun fileFilterByExtension(extension: String) =
            FileFilter { file -> file.isDirectory || file.name.endsWith(".$extension") }


    private fun File.listFilesRecursively(filter: FileFilter?): List<File> {
        if (!this.isDirectory) return emptyList()
        return this.listFiles(filter).fold(emptyList<File>()) { list, file ->
            if (file.isFile) {
                list + file
            } else {
                list + file.listFilesRecursively(filter)
            }
        }
    }

}