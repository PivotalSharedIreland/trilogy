import org.gradle.api.plugins.*
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

configure<JavaPluginConvention> {
    setSourceCompatibility(1.7)
    setTargetCompatibility(1.7)
}


dependencies {
    compile(kotlinModule("stdlib", version = "1.0.4"))
    compile("org.springframework.boot:spring-boot-starter-jdbc")
    compile("org.springframework.boot:spring-boot-starter:1.4.2.RELEASE")
    compile("org.jetbrains.kotlin:kotlin-stdlib:1.0.4")
    compile("commons-cli:commons-cli:1.3.1")
    compile("org.flywaydb:flyway-core:4.0.3")

    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("org.jetbrains.spek:spek:1.0.+")
    testCompile("org.amshove.kluent:kluent:1.4")
}
