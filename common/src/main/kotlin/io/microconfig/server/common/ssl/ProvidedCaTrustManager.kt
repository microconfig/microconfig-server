package io.microconfig.server.common.ssl

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class ProvidedCaTrustManager(rootCaPath: File) : X509TrustManager {
    private val rootCa: X509Certificate = loadCertificate(rootCaPath)

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf(rootCa)
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
        if (certs.isEmpty()) throw CertificateException("Certificate chain is empty")
        val last = certs[certs.size - 1]
        if (rootCa != last) throw CertificateException(errorMessage(last))
    }

    private fun errorMessage(provided: X509Certificate): String {
        return "Last certificate in chain: [${provided.subjectDN.name}] doesn't match expected root ca: [${rootCa.subjectDN.name}]"
    }

    override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
    }

}

private fun loadCertificate(rootCa: File): X509Certificate {
    return try {
        if (!rootCa.exists()) throw RuntimeException("Can't load Root CA from " + rootCa.absolutePath + " Path doesn't exist")
        val fact = CertificateFactory.getInstance("X.509")
        fact.generateCertificate(FileInputStream(rootCa)) as X509Certificate
    } catch (e: CertificateException) {
        throw RuntimeException("Can't load Root CA from " + rootCa.absolutePath + " error:" + e.toString())
    } catch (e: FileNotFoundException) {
        throw RuntimeException("Can't load Root CA from " + rootCa.absolutePath + " error:" + e.toString())
    }
}
