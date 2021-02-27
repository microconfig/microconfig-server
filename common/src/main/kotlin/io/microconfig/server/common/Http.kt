package io.microconfig.server.common

import java.net.http.HttpClient
import java.security.SecureRandom
import javax.net.ssl.SSLContext

fun httpClient(): HttpClient {
    val ssl = SSLContext.getInstance("TLSv1.2")
    ssl.init(null, null, SecureRandom())
    return HttpClient.newBuilder().sslContext(ssl).build()
}

fun httpClient(sslContext: SSLContext): HttpClient {
    return HttpClient.newBuilder().sslContext(sslContext).build()
}