package io.microconfig.server.common.ssl

import java.io.File
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

fun trustAll(): SSLContext {
    return try {
        SSLContext.getInstance("TLS")
            .apply { init(null, arrayOf<TrustManager>(AllowAllTrustManager()), SecureRandom()) }
    } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException("Can't init TLS context: $e")
    } catch (e: KeyManagementException) {
        throw RuntimeException("Can't init TLS context: $e")
    }
}

fun defaultTrust(): SSLContext {
    return SSLContext.getDefault()
}

fun rootCa(path: String): SSLContext {
    val rootCa = File(path)
    val sc = SSLContext.getInstance("TLS")
    val trustManager = ProvidedCaTrustManager(rootCa)
    sc.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
    return sc
}
