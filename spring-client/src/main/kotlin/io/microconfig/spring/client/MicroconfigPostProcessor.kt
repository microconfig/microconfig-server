package io.microconfig.spring.client

import io.microconfig.server.client.ClientConfig
import io.microconfig.server.client.MicroconfigClient
import io.microconfig.server.client.MicroconfigClientImpl
import io.microconfig.server.client.MicroconfigRequest
import io.microconfig.server.client.dto.ServiceTemplate
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.annotation.Order
import org.springframework.core.env.ConfigurableEnvironment
import java.io.File
import java.nio.file.Files.createDirectory
import java.nio.file.Files.writeString
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import java.time.Duration

@Order
class MicroconfigPostProcessor : EnvironmentPostProcessor {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val name = environment.getProperty("spring.application.name") ?: return
        val enabled = environment.getProperty("spring.microconfig.enabled") ?: return
        if (enabled != "true") return

        val config = client(environment).configMaps(request(name, environment)).first()
        val configDir = File("config")
        saveTemplates(configDir, config.templates)
        environment.propertySources.addLast(MicroconfigPropertySource(config.content))
    }

    private fun request(name: String, environment: ConfigurableEnvironment): MicroconfigRequest {
        val env = environment.getProperty("spring.microconfig.env") ?: "default"
        println("Microconfig enabled for $name in $env environment")
        val ref = environment.getProperty("spring.microconfig.ref")
        return MicroconfigRequest(name, env, "app", ref)
    }

    private fun client(environment: ConfigurableEnvironment): MicroconfigClient {
        val server = environment.getProperty("spring.microconfig.server") ?: "http://localhost:8080"
        val timeout = Duration.ofSeconds(1)
        return MicroconfigClientImpl(ClientConfig(server, timeout))
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