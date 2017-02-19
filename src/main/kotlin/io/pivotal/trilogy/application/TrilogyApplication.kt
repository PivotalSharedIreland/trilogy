package io.pivotal.trilogy.application

import io.pivotal.trilogy.i18n.MessageCreator.getI18nMessage
import org.springframework.beans.factory.UnsatisfiedDependencyException
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import java.net.ConnectException

@SpringBootApplication
@Import(TrilogyApplicationConfiguration::class)
open class TrilogyApplication {


    companion object {
        @JvmStatic fun main(args: Array<String>) {
            try {
                SpringApplication.run(TrilogyApplication::class.java, *args)
            } catch(e: IllegalStateException) {
                if (e.isCausedByConnectionFault) {
                    println(getI18nMessage("connectionFailure", listOf("${e.cause}")))
                }
                throw e
            } catch (e: UnsatisfiedDependencyException) {
                println(getI18nMessage("applicationUsage"))
                throw e
            }
        }

        private val Throwable.isCausedByConnectionFault: Boolean get() {
            return (this.cause?.cause is ConnectException) or (this.cause?.cause?.cause is ConnectException)
        }
    }

}


