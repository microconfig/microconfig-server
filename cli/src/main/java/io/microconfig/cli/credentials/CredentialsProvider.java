package io.microconfig.cli.credentials;

import io.microconfig.cli.CliException;

import java.net.http.HttpRequest;

public class CredentialsProvider {
    private static final VaultToken vaultToken = new VaultToken();

    public HttpRequest.Builder addCredentials(String type, HttpRequest.Builder request, String[] args) {
        switch (type) {
            case "vault-token":
                return vaultToken.addCredentials(request, args);
            default:
                throw new CliException("Unsupported auth value: " + type, 123);
        }
    }
}
