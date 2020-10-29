package io.microconfig.cli.ssl;

import io.microconfig.cli.CliException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class TrustManagerFactory {

    public static SSLContext trustAll() {
        try {
            var sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new AllowAllTrustManager()}, new java.security.SecureRandom());
            return sc;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new CliException("Can't init TLS context: " + e.toString(), 1000);
        }

    }

    public static SSLContext defaultTrust() {
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new CliException("Can't init TLS context: " + e.toString(), 1000);
        }
    }

    public static SSLContext rootCa(String path) {
        try {
            var rootCa = new File(path);
            var sc = SSLContext.getInstance("TLS");
            var trustManager = new ProvidedCaTrustManager(rootCa);
            sc.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
            return sc;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new CliException("Can't init TLS context: " + e.toString(), 1000);
        }
    }

}
