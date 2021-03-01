package io.microconfig.server.client

import io.microconfig.server.common.ssl.tls12
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect.NORMAL
import java.time.Duration
import java.time.Duration.ofSeconds
import javax.net.ssl.SSLContext

data class ClientConfig(val server: String, val timeout: Duration = ofSeconds(5), val sslContext: SSLContext? = null) {

    fun http(): HttpClient {
        val ssl = sslContext ?: tls12()
        return HttpClient.newBuilder()
            .connectTimeout(timeout)
            .sslContext(ssl)
            .followRedirects(NORMAL)
            .build()
    }
}
