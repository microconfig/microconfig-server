package io.microconfig.server.vault

import io.microconfig.server.vault.credentials.KubernetesTokenCredentials
import io.microconfig.server.vault.credentials.VaultAppRoleCredentials
import io.microconfig.server.vault.credentials.VaultCredentials
import io.microconfig.server.vault.credentials.VaultTokenCredentials
import io.microconfig.server.vault.exceptions.VaultAuthException

data class VaultConfig(val address: String, val credentials: VaultCredentials)

fun vaultConfig(config: Map<String, String>): VaultConfig {
    val address = getValue(config, "microconfig.vault.address")
    val auth = auth(address, config)
    return VaultConfig(address, auth)
}

private fun auth(address: String, config: Map<String, String>): VaultCredentials {
    return when (val auth = getValue(config, "microconfig.vault.auth")) {
        "token" -> token(config)
        "kubernetes" -> kubernetes(address, config)
        "approle" -> approle(address, config)
        else -> throw VaultAuthException("Unsupported auth type: $auth")
    }
}

private fun token(config: Map<String, String>): VaultCredentials {
    val token = getValue(config, "microconfig.vault.token")
    return VaultTokenCredentials(token)
}

private fun kubernetes(address: String, config: Map<String, String>): VaultCredentials {
    val path = getValue(config, "microconfig.vault.kubernetes.path")
    val role = getValue(config, "microconfig.vault.kubernetes.role")
    val jwt = getValue(config, "microconfig.vault.kubernetes.jwt")
    return KubernetesTokenCredentials(address, path, role, jwt)
}

private fun approle(address: String, config: Map<String, String>): VaultCredentials {
    val path = getValue(config, "microconfig.vault.approle.path")
    val role = getValue(config, "microconfig.vault.approle.role")
    val secret = getValue(config, "microconfig.vault.approle.secret")
    return VaultAppRoleCredentials(address, path, role, secret)
}

private fun getValue(config: Map<String, String>, key: String): String {
    return config[key] ?: throw VaultAuthException("Vault config missing: $key")
}