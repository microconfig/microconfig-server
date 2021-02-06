package io.microconfig.server.client

import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect.NORMAL
import java.time.Duration
import java.time.Duration.ofSeconds
import javax.net.ssl.SSLContext

data class ClientConfig(val server: String, val timeout: Duration = ofSeconds(5), val sslContext: SSLContext?) {

    fun http(): HttpClient {
        val ssl = sslContext ?: SSLContext.getInstance("TLSv1.2").apply { init(null, null, null) }
        return HttpClient.newBuilder()
            .connectTimeout(timeout)
            .sslContext(ssl)
            .followRedirects(NORMAL)
            .build()
    }
}
