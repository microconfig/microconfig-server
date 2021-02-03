package io.microconfig.server.common

import java.net.http.HttpClient
import javax.net.ssl.SSLContext

// TODO add support for different ssl cases
fun httpClient(): HttpClient {
    val ssl = SSLContext.getInstance("TLSv1.2")
    ssl.init(null, null, null)
    return HttpClient.newBuilder().sslContext(ssl).build()
}