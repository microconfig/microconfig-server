package io.microconfig.server.common.ssl

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class AllowAllTrustManager : X509TrustManager {
    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }

    override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
    }

    override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
    }
}