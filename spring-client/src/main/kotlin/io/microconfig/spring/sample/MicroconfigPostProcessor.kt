package io.microconfig.spring.sample

import io.microconfig.server.client.dto.ServiceTemplate
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import java.io.File
import java.nio.file.Files.createDirectory
import java.nio.file.Files.writeString
import java.nio.file.StandardOpenOption.*

class MicroconfigPostProcessor(private val client: MicroconfigSpringClient) : EnvironmentPostProcessor {
    constructor() : this(MicroconfigSpringClient())

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val name = environment.getProperty("spring.application.name") ?: return
        val enabled = environment.getProperty("microconfig.enabled") ?: return
        if (enabled != "true") return

        val config = client.fetchConfig(name, environment)
        val configDir = File("config")
        saveTemplates(configDir, config.templates)
        environment.propertySources.addLast(MicroconfigPropertySource(config.content))
    }

    private fun saveTemplates(configDir: File, templates: List<ServiceTemplate>) {
        if (!configDir.exists()) createDirectory(configDir.toPath())
        templates.forEach {
            writeString(
                configDir.resolve(it.file).toPath(), it.content,
                TRUNCATE_EXISTING, WRITE, CREATE
            )
        }
    }

}