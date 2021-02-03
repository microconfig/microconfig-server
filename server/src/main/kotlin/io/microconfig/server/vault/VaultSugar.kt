package io.microconfig.server.vault

import com.fasterxml.jackson.databind.JsonNode
import io.microconfig.server.common.json
import io.microconfig.server.vault.exceptions.VaultAuthException
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun HttpRequest.Builder.build(config: VaultConfig): HttpRequest {
    return this.setHeader("X-Vault-Token", config.credentials.getToken())
        .timeout(config.timeout)
        .build()
}

fun HttpResponse<String>.vaultResponse(): JsonNode {
    val node = this.json()
    if (statusCode() == 200) return node

    if (node["errors"] != null) {
        throw VaultAuthException("Vault request failed with errors: ${node["errors"]}")
    } else {
        throw VaultAuthException("Vault request failed with http status: ${this.statusCode()}")
    }
}
