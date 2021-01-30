package io.microconfig.server.common

import java.io.IOException
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext

object Http {
    private val log = logger()
    private val httpClient = client()

    fun client(): HttpClient {
        return try {
            val ssl = SSLContext.getInstance("TLSv1.2")
            ssl.init(null, null, null)
            HttpClient.newBuilder().sslContext(ssl).build()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init ssl", e)
        } catch (e: Exception) {
            throw RuntimeException("Failed to build http client", e)
        }
    }

    fun httpSend(request: HttpRequest): HttpResponse<String> {
        return try {
            httpClient.send(request, BodyHandlers.ofString())
        } catch (e: HttpConnectTimeoutException) {
            throw RuntimeException("HTTP timeout: " + request.uri().toString())
        } catch (e: IOException) {
            log.error("Failed http call: {}", e.javaClass)
            throw RuntimeException(e.javaClass.toString() + ": " + e.message)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException()
        }
    }
}