package io.microconfig.server.vault.credentials

import com.fasterxml.jackson.annotation.JsonProperty
import io.microconfig.server.common.Http.client
import io.microconfig.server.common.httpPost
import io.microconfig.server.common.logger
import io.microconfig.server.common.send
import io.microconfig.server.common.toJson
import io.microconfig.server.vault.vaultResponse
import java.net.http.HttpRequest
import java.time.Duration

class VaultAppRoleCredentials(
    val address: String,
    val path: String,
    val role: String,
    val secret: String
) : VaultCredentials {
    private val log = logger()
    private val http = client()
    private var token: String? = null

    override fun getToken(): String {
        if (token != null) return token!!

        val request = request()
        val node = request.send(http).vaultResponse()
        log.debug("Fetched token with AppRole")
        token = node.path("auth").path("client_token").asText()
        return token!!
    }

    private fun request(): HttpRequest {
        val body = Request(role, secret).toJson()
        val url = "$address/v1/auth/$path/login"
        return httpPost(url, body)
            .timeout(Duration.ofSeconds(2))
            .build()
    }

    data class Request(
        @JsonProperty("role_id") val role: String,
        @JsonProperty("secret_id") val secret: String
    )
}