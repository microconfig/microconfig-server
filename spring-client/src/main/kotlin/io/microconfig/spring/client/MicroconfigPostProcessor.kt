package io.microconfig.spring.client

import io.microconfig.server.client.ClientConfig
import io.microconfig.server.client.MicroconfigClient
import io.microconfig.server.client.MicroconfigClientImpl
import io.microconfig.server.client.MicroconfigRequest
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.annotation.Order
import org.springframework.core.env.ConfigurableEnvironment
import java.time.Duration

@Order
class MicroconfigPostProcessor : EnvironmentPostProcessor {
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        // first call during bootstrap can be ignored
        val name = environment.getProperty("spring.application.name") ?: return
        val enabled = environment.getProperty("spring.microconfig.enabled") ?: return
        if (enabled != "true") return
        val env = env(environment)
        println("Microconfig enabled for $name in $env environment")

        val configs = client(environment).configMaps(MicroconfigRequest(name, env, "app")).first()
        environment.propertySources.addLast(MicroconfigPropertySource(configs.content))
    }

    private fun client(environment: ConfigurableEnvironment): MicroconfigClient {
        val server = "http://localhost:8080"
        val timeout = Duration.ofSeconds(1)
        return MicroconfigClientImpl(ClientConfig(server, timeout))
    }

    private fun env(environment: ConfigurableEnvironment): String {
        return environment.getProperty("spring.microconfig.env") ?: "default"
    }
}