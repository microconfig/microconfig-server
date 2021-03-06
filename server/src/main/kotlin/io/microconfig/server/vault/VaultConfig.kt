package io.microconfig.server.vault

import io.microconfig.server.vault.credentials.KubernetesTokenCredentials
import io.microconfig.server.vault.credentials.VaultAppRoleCredentials
import io.microconfig.server.vault.credentials.VaultCredentials
import io.microconfig.server.vault.credentials.VaultTokenCredentials
import io.microconfig.server.vault.exceptions.VaultAuthException
import java.time.Duration
import java.time.Duration.ofSeconds

data class VaultConfig(val address: String, val timeout: Duration, val credentials: VaultCredentials)

const val DEFAULT_TIMEOUT = 2L

fun vaultConfig(config: Map<String, String>): VaultConfig {
    val address = getValue(config, "microconfig.vault.address")
    val timeout = timeout(config)
    val auth = auth(address, timeout, config)
    return VaultConfig(address, timeout, auth)
}

private fun timeout(config: Map<String, String>): Duration {
    val t = config["microconfig.vault.timeout"]?.toLong() ?: DEFAULT_TIMEOUT
    return ofSeconds(t)
}

private fun auth(address: String, timeout: Duration, config: Map<String, String>): VaultCredentials {
    return when (val auth = getValue(config, "microconfig.vault.auth")) {
        "token" -> token(config)
        "kubernetes" -> kubernetes(address, timeout, config)
        "approle" -> approle(address, timeout, config)
        else -> throw VaultAuthException("Unsupported auth type: $auth")
    }
}

private fun token(config: Map<String, String>): VaultCredentials {
    val token = getValue(config, "microconfig.vault.token")
    return VaultTokenCredentials(token)
}

private fun kubernetes(address: String, timeout: Duration, config: Map<String, String>): VaultCredentials {
    val path = getValue(config, "microconfig.vault.kubernetes.path")
    val role = getValue(config, "microconfig.vault.kubernetes.role")
    val jwt = getValue(config, "microconfig.vault.jwt")
    return KubernetesTokenCredentials(address, timeout, path, role, jwt)
}

private fun approle(address: String, timeout: Duration, config: Map<String, String>): VaultCredentials {
    val path = getValue(config, "microconfig.vault.approle.path")
    val role = getValue(config, "microconfig.vault.approle.role")
    val secret = getValue(config, "microconfig.vault.approle.secret")
    return VaultAppRoleCredentials(address, timeout, path, role, secret)
}

private fun getValue(config: Map<String, String>, key: String): String {
    return config[key] ?: throw VaultAuthException("Vault config missing: $key")
}