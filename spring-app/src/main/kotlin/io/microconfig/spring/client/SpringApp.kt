package io.microconfig.spring.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class SpringApp

fun main(args: Array<String>) {
    runApplication<SpringApp>(*args)
}

@Configuration
class Config {
    @Bean
    fun foo(
        @Value("\${foo}") foo: String,
        @Value("\${remote.prop1}") prop1: String,
        @Value("\${remote.prop2}") prop2: String
    ): String {
        println("Value fetched from remote: $foo, $prop1, $prop2")
        return foo
    }
}
