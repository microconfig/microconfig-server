package io.microconfig.server.client

import com.fasterxml.jackson.databind.JsonNode
import io.microconfig.server.common.GET
import io.microconfig.server.common.convert
import io.microconfig.server.common.json
import io.microconfig.server.common.send
import java.net.http.HttpRequest

class MicroconfigClientImpl(private val config: ClientConfig) : MicroconfigClient {
    private val http = config.http()

    override fun configs(request: MicroconfigRequest): List<ServiceConfig> {
        return send(request).map { it.convert(ServiceConfig::class.java) }

    }

    private fun send(request: MicroconfigRequest): JsonNode {
        val get = GET(url(request)).addHeaders(request)
        val response = get.send(http)
        if (response.statusCode() == 200) {
            return response.body().json()
        } else {
            val error = response.json().get("error").asText()
            throw MicroconfigException(error, response.statusCode())
        }
    }

    private fun url(request: MicroconfigRequest): String {
        return "${config.server}/api/configs/${request.component}/${request.env}"
    }

    private fun HttpRequest.Builder.addHeaders(request: MicroconfigRequest): HttpRequest {
        request.type?.let { this.setHeader("X-TYPE", it) }
        request.ref?.let { this.setHeader("X-REF", it) }
        request.vars.forEach { (key: String, value: String) -> this.header("X-VAR", "$key=$value") }
        return this.build()
    }
}