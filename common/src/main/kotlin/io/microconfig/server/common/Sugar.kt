package io.microconfig.server.common

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.net.URI.create
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers.ofString
import java.util.UUID

private val mapper = ObjectMapper()

fun httpGet(url: String): HttpRequest.Builder {
    return http(url).GET()
}

fun httpPost(url: String, body: String): HttpRequest.Builder {
    return http(url).POST(ofString(body))
        .setHeader("Content-Type", "application/json")
}

fun httpPost(url: String, body: JsonNode): HttpRequest.Builder {
    return http(url).POST(ofString(body.toString()))
        .setHeader("Content-Type", "application/json")
}

fun httpDelete(url: String): HttpRequest.Builder = http(url).DELETE()

private fun http(url: String): HttpRequest.Builder = newBuilder(create(url))

fun HttpRequest.send(client: HttpClient): HttpResponse<String> = client.send(this, ofString())

fun HttpResponse<String>.json(): JsonNode = mapper.readTree(this.body())

fun Any.toJson(): String = mapper.writeValueAsString(this)

fun String.json(): JsonNode = mapper.readTree(this)

fun objectNode(): ObjectNode = mapper.createObjectNode()

fun arrayNode(): ArrayNode = mapper.createArrayNode()

fun JsonNode.uuid(name: String): UUID = mapper.convertValue(this[name], UUID::class.java)

fun JsonNode.stream(key: String): Sequence<JsonNode> = this[key].elements().asSequence()