package io.microconfig.server.vault

import com.fasterxml.jackson.databind.JsonNode
import io.microconfig.server.common.Http
import io.microconfig.server.common.httpGet
import io.microconfig.server.common.logger
import io.microconfig.server.common.send
import io.microconfig.server.vault.exceptions.VaultSecretNotFound
import java.time.Duration

class VaultClientImpl(private val config: VaultConfig) : VaultClient {
    private val log = logger()
    private val http = Http.client()

    override fun fetchKV(property: String): String {
        val (path, key) = pathKey(property)
        log.debug("Fetching {} {}", path, key)
        val node = getPath(path, config.credentials.getToken())
        val value = node
            .path("data")
            .path("data")[key] ?: throw VaultSecretNotFound(property)
        return value.asText()
    }

    private fun pathKey(property: String): Pair<String, String> {
        val dotIndex = property.lastIndexOf('.')
        val path = property.substring(0, dotIndex)
        val key = property.substring(dotIndex + 1)
        return Pair(path, key)
    }

    private fun getPath(path: String, token: String): JsonNode {
        val url = kvUrl(path)
        log.debug("Calling Vault {}", url)
        val request = httpGet(url)
            .setHeader("X-Vault-Token", token)
            .timeout(Duration.ofSeconds(2))
            .build()

        return request
            .send(http)
            .vaultResponse()
    }

    private fun kvUrl(path: String): String {
        val splitPath = splitPath(path)
        return "${config.address}/v1/${splitPath[0]}/data/${splitPath[1]}"
    }

    private fun splitPath(path: String): Array<String> {
        val slash = path.indexOf('/')
        return arrayOf(path.substring(0, slash), path.substring(slash))
    }
}

