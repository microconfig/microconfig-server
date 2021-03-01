package io.microconfig.spring.sample

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(Config::class)
class SpringApp

private val logger = LoggerFactory.getLogger(SpringApp::class.java)

fun main(args: Array<String>) {
    val context = runApplication<SpringApp>(*args)
    val config = context.getBean(Config::class.java)
    logger.info("App Config: $config")
    logger.debug("This is debug message")
}