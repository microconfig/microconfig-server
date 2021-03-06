package io.microconfig.server.vault.credentials

import io.microconfig.server.common.POST
import io.microconfig.server.common.httpClient
import io.microconfig.server.common.logger
import io.microconfig.server.common.send
import io.microconfig.server.common.toJson
import io.microconfig.server.vault.vaultResponse
import java.net.http.HttpRequest
import java.time.Duration

class KubernetesTokenCredentials(
    val address: String,
    val timeout: Duration,
    val path: String,
    val role: String,
    val jwt: String
) : VaultCredentials {

    private val log = logger()
    private val http = httpClient()
    private var token: String? = null

    override fun getToken(): String {
        if (token != null) return token!!

        val node = request().send(http).vaultResponse()
        token = node.path("auth").path("client_token").asText()
        log.debug("Fetched token with k8s role $role")
        return token!!
    }

    private fun request(): HttpRequest {
        val body = Request(role, jwt).toJson()
        val url = "$address/v1/auth/$path/login"
        return POST(url, body)
            .timeout(timeout)
            .build()
    }

    data class Request(
        val role: String,
        val jwt: String
    )

}