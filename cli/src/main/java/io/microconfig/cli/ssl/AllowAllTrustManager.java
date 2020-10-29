package io.microconfig.cli.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class AllowAllTrustManager implements X509TrustManager {

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                                   String authType) {
    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                                   String authType) {
    }
}
