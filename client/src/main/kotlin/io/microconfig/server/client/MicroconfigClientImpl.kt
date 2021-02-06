package io.microconfig.server.client

import com.fasterxml.jackson.databind.JsonNode
import io.microconfig.server.client.dto.ServiceConfigMap
import io.microconfig.server.client.dto.ServiceConfigRaw
import io.microconfig.server.common.GET
import io.microconfig.server.common.convert
import io.microconfig.server.common.json
import io.microconfig.server.common.send
import java.net.http.HttpRequest

class MicroconfigClientImpl(private val config: ClientConfig) : MicroconfigClient {
    private val http = config.http()

    override fun configs(request: MicroconfigRequest): List<ServiceConfigRaw> {
        val url = urlRaw(request)
        return send(request, url).map { it.convert(ServiceConfigRaw::class.java) }
    }

    override fun configMaps(request: MicroconfigRequest): List<ServiceConfigMap> {
        val url = urlMap(request)
        return send(request, url).map { it.convert(ServiceConfigMap::class.java) }
    }

    private fun send(request: MicroconfigRequest, url: String): JsonNode {
        val get = GET(url).addHeaders(request)
        val response = get.send(http)
        if (response.statusCode() == 200) {
            return response.body().json()
        } else {
            val error = response.json().get("error").asText()
            throw MicroconfigException(error, response.statusCode())
        }
    }

    private fun urlRaw(request: MicroconfigRequest): String {
        return "${config.server}/api/configs/${request.component}/${request.env}"
    }

    private fun urlMap(request: MicroconfigRequest): String {
        return "${config.server}/api/configs-map/${request.component}/${request.env}"
    }

    private fun HttpRequest.Builder.addHeaders(request: MicroconfigRequest): HttpRequest {
        request.type?.let { this.setHeader("X-TYPE", it) }
        request.ref?.let { this.setHeader("X-REF", it) }
        request.vars.forEach { (key: String, value: String) -> this.header("X-VAR", "$key=$value") }
        return this.build()
    }
}