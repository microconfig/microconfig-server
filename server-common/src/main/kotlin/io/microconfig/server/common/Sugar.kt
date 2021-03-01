package io.microconfig.server.common

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.IOException
import java.net.ConnectException
import java.net.URI.create
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers.ofString
import java.util.*
import javax.net.ssl.SSLException

private val mapper = ObjectMapper().registerModule(KotlinModule())

fun GET(url: String): HttpRequest.Builder {
    return http(url).GET()
}

fun POST(url: String, body: String): HttpRequest.Builder {
    return http(url).POST(ofString(body))
        .setHeader("Content-Type", "application/json")
}

fun POST(url: String, body: JsonNode): HttpRequest.Builder {
    return http(url).POST(ofString(body.toString()))
        .setHeader("Content-Type", "application/json")
}

fun httpDelete(url: String): HttpRequest.Builder = http(url).DELETE()

private fun http(url: String): HttpRequest.Builder = newBuilder(create(url))

fun HttpRequest.send(client: HttpClient): HttpResponse<String> {
    try {
        return client.send(this, ofString())
    } catch (e: HttpConnectTimeoutException) {
        throw HttpException("HTTP timeout: ${this.uri()}", e)
    } catch (e: IOException) {
        throw e.toHttpException(this.uri().toString())
    }
}

fun IOException.toHttpException(url: String): HttpException {
    val cause = this.cause
    return when (cause) {
        is ConnectException -> HttpException("Can't connect to $url. ${cause.message}", cause)
        is SSLException -> HttpException("SSL exception: $url: ${cause.message}", cause)
        else -> HttpException("IO exception: $url", this)
    }
}

fun HttpResponse<String>.json(): JsonNode = mapper.readTree(this.body())

fun <T> T.toOptional(): Optional<T> = Optional.of(this!!)

fun Any.toJson(): String = mapper.writeValueAsString(this)

fun String.json(): JsonNode = mapper.readTree(this)

fun <T> JsonNode.convert(clazz: Class<T>): T = mapper.convertValue(this, clazz)

fun objectNode(): ObjectNode = mapper.createObjectNode()

fun arrayNode(): ArrayNode = mapper.createArrayNode()

fun JsonNode.uuid(name: String): UUID = mapper.convertValue(this[name], UUID::class.java)

fun JsonNode.stream(key: String): Sequence<JsonNode> = this[key].elements().asSequence()