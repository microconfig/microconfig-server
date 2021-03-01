package io.microconfig.server.common

import io.microconfig.server.common.ssl.tls12
import java.net.http.HttpClient
import javax.net.ssl.SSLContext

fun httpClient(): HttpClient {
    return HttpClient.newBuilder().sslContext(tls12()).build()
}

fun httpClient(sslContext: SSLContext): HttpClient {
    return HttpClient.newBuilder().sslContext(sslContext).build()
}