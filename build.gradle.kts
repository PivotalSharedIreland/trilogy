import org.gradle.api.plugins.*
import org.gradle.api.tasks.testing.Test
import org.gradle.script.lang.kotlin.*

buildscript {
    repositories {
        gradleScriptKotlin()
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:1.4.2.RELEASE")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.0.4")
    }
}

apply {
    plugin("kotlin")
    plugin("java")
    plugin("org.springframework.boot")
    plugin<ApplicationPlugin>()
}

configure<ApplicationPluginConvention> {
    mainClassName = "io.pivotal.trilogy.application.TrilogyApplication"
}

repositories {
    gradleScriptKotlin()
    jcenter()
}


val oracleBootstrapClasses = "io/pivotal/trilogy/live/oracle/bootstrap/**"
val oracleBootstrap = task<Test>("oracleBootstrap") {
    include(oracleBootstrapClasses)
}

val oracleTests = task<Test>("oracleTests") {
    dependsOn(oracleBootstrap)
    include("io/pivotal/trilogy/live/oracle/**")
    exclude(oracleBootstrapClasses)
}

val test = tasks.getByName("test") as Test
test.apply {
    exclude("io/pivotal/trilogy/live/**")
}

task<Test>("testAll") {
    dependsOn(oracleTests, test)
}


configure<JavaPluginConvention> {
    setSourceCompatibility(1.7)
    setTargetCompatibility(1.7)
}


dependencies {
    compile(kotlinModule("stdlib", version = "1.0.4"))
    compile("org.springframework.boot:spring-boot-starter-jdbc")
    compile("org.springframework.boot:spring-boot-starter:1.4.2.RELEASE")
    compile("commons-cli:commons-cli:1.3.1")
    compile("org.flywaydb:flyway-core:4.0.3")

    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("org.jetbrains.spek:spek:1.0.+")
    testCompile("org.amshove.kluent:kluent:1.4")
}
