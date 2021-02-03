package io.microconfig.server.vault

import com.fasterxml.jackson.databind.JsonNode
import io.microconfig.server.common.GET
import io.microconfig.server.common.httpClient
import io.microconfig.server.common.logger
import io.microconfig.server.common.send
import io.microconfig.server.vault.exceptions.VaultSecretNotFound

class VaultClientImpl(private val config: VaultConfig) : VaultClient {
    private val log = logger()
    private val http = httpClient()

    override fun fetchKV(property: String): String {
        val (path, key) = pathKey(property)
        log.debug("Fetching $path $key")
        val node = getPath(path)
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

    private fun getPath(path: String): JsonNode {
        return GET(kvUrl(path))
            .build(config)
            .send(http)
            .vaultResponse()
    }

    private fun kvUrl(path: String): String {
        val (root, key) = splitPath(path)
        return "${config.address}/v1/$root/data/$key"
    }

    private fun splitPath(path: String): Pair<String, String> {
        val slash = path.indexOf('/')
        val root = path.substring(0, slash) // secret engine path like /secret
        val key = path.substring(slash + 1) // actual key path like dev/database
        return Pair(root, key)
    }
}

