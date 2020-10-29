package io.microconfig.cli.ssl;

import io.microconfig.cli.CliException;

import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class ProvidedCaTrustManager implements X509TrustManager {
    private final X509Certificate rootCa;

    public ProvidedCaTrustManager(File rootCaPath) {
        rootCa = loadCertificate(rootCaPath);
    }

    private static X509Certificate loadCertificate(File rootCa) {
        try {
            if (!rootCa.exists())
                throw new CliException("Can't load Root CA from " + rootCa.getAbsolutePath() + " Path doesn't exist", 1000);
            var fact = CertificateFactory.getInstance("X.509");
            return (X509Certificate) fact.generateCertificate(new FileInputStream(rootCa));
        } catch (CertificateException | FileNotFoundException e) {
            throw new CliException("Can't load Root CA from " + rootCa.getAbsolutePath() + " error:" + e.toString(), 1000);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{rootCa};
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        if (certs.length == 0) throw new CertificateException("Certificate chain is empty");
        var last = certs[certs.length - 1];
        if (!rootCa.equals(last)) throw new CertificateException(errorMessage(last));
    }

    private String errorMessage(X509Certificate provided) {
        return String.format("Last certificate in chain: [%s] doesn't match expected root ca: [%s]",
                provided.getSubjectDN().getName(),
                rootCa.getSubjectDN().getName()
        );
    }

    @Override
    public void checkClientTrusted(X509Certificate[] certs,
                                   String authType) {

    }
}
