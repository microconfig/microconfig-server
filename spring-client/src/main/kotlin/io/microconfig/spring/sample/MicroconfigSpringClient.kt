package io.microconfig.spring.sample

import io.microconfig.server.client.ClientConfig
import io.microconfig.server.client.MicroconfigClient
import io.microconfig.server.client.MicroconfigClientImpl
import io.microconfig.server.client.MicroconfigRequest
import io.microconfig.server.client.dto.ServiceConfigMap
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import java.time.Duration

const val VARS_PREFIX_LENGTH = 17

class MicroconfigSpringClient {

    fun fetchConfig(name: String, env: ConfigurableEnvironment): ServiceConfigMap {
        return client(env).configMaps(request(name, env)).first()
    }

    private fun client(environment: ConfigurableEnvironment): MicroconfigClient {
        val server = environment.getProperty("microconfig.server") ?: "http://localhost:8080"
        val timeout = Duration.ofSeconds(1)
        return MicroconfigClientImpl(ClientConfig(server, timeout))
    }

    private fun request(name: String, environment: ConfigurableEnvironment): MicroconfigRequest {
        val env = environment.getProperty("microconfig.env") ?: "default"
        println("Microconfig enabled for $name in $env environment")
        val ref = environment.getProperty("microconfig.ref")
        val vars = extractVars(environment)
        return MicroconfigRequest(name, env, "app", ref, vars)
    }

    private fun extractVars(environment: ConfigurableEnvironment): Map<String, String> {
        val props = fromSpringEnv(environment)
        val envs = fromEnvVars()
        return props + envs
    }

    private fun fromSpringEnv(environment: ConfigurableEnvironment): Map<String, String> {
        return environment.propertySources
            .filterIsInstance<MapPropertySource>()
            .flatMap { it.source.entries }
            .filter { (k, _) -> k.startsWith("microconfig.") }
            .map { (k, v) -> extractPropVarName(k) to v.toString() }
            .toMap()
    }

    private fun extractPropVarName(key: String): String {
        return when {
            key.startsWith("microconfig.vars.") -> key.substring(VARS_PREFIX_LENGTH)
            else -> key
        }
    }

    private fun fromEnvVars(): Map<String, String> {
        return System.getenv().filter { (k, _) -> k.startsWith("MICROCONFIG_") }
            .map { (k, v) -> extractEnvVarName(k) to v }
            .toMap()
    }

    private fun extractEnvVarName(key: String): String {
        val prefix = when {
            key.startsWith("MICROCONFIG_VARS_") -> VARS_PREFIX_LENGTH
            else -> 0
        }
        return key.substring(prefix).replace('_', '.').toLowerCase()
    }

}