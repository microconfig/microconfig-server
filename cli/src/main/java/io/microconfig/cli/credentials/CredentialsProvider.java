package io.microconfig.cli.credentials;

import io.microconfig.cli.CliException;

import java.net.http.HttpRequest;

public class CredentialsProvider {
    public HttpRequest.Builder addCredentials(HttpRequest.Builder request, String auth) {
        switch (auth) {
            case "vault-kubernetes":
                return request.setHeader("X-AUTH-TYPE", "VAULT_KUBERNETES");
            case "vault-token":
                return request.setHeader("X-AUTH-TYPE", "VAULT_TOKEN");
            case "vault-approle":
                return request.setHeader("X-AUTH-TYPE", "VAULT_APP_ROLE");
            default:
                throw new CliException("Unsupported auth value: " + auth, 123);
        }
    }
}
