package io.microconfig.server.vault

import com.fasterxml.jackson.databind.JsonNode
import io.microconfig.server.common.json
import io.microconfig.server.vault.exceptions.VaultAuthException
import java.net.http.HttpResponse

class VaultCommon

fun HttpResponse<String>.vaultResponse(): JsonNode {
    val node = this.json()
    if (this.statusCode() != 200) {
        if (node["errors"] != null) {
            throw VaultAuthException("Vault request failed with errors: ${node["errors"]}")
        } else {
            throw VaultAuthException("Vault request failed with http status: ${this.statusCode()}")
        }
    }
    return node
}
