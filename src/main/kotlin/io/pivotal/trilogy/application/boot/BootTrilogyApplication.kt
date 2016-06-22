package io.pivotal.trilogy.application.boot

import io.pivotal.trilogy.testrunner.TestRunnerConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(TestRunnerConfiguration::class)
open class BootTrilogyApplication {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(BootTrilogyApplication::class.java, *args)
        }
    }

}
