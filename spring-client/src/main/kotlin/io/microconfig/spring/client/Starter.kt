package io.microconfig.spring.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class Starter

fun main(args: Array<String>) {
    runApplication<Starter>(*args)
}

@Configuration
class Config {
    @Bean
    fun micro(@Value("\${database.url}") value: String): String {
        println(value)
        return value
    }
}
