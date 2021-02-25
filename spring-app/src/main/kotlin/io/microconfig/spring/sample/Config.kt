package io.microconfig.spring.sample

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "config")
data class Config(
    val foo: String,
    val secret: String,
    val prop1: String,
    val prop2: String
)