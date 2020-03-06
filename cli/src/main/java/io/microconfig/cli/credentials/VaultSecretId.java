package io.microconfig.cli.credentials;

import io.microconfig.cli.CliException;
import io.microconfig.cli.CliFlags;

import java.net.http.HttpRequest;

public class VaultSecretId implements Credentials {
    @Override
    public HttpRequest.Builder addCredentials(HttpRequest.Builder request, String[] args) {
        var flags = new CliFlags(args);
        var secret = flags.vaultSecretId().orElseGet(this::fromEnv);
        return addSecretId(request, secret);
    }

    private String fromEnv() {
        var token = System.getenv("VAULT_SECRET_ID");
        if (token == null || token.isEmpty())
            throw new CliException("Vault secret id not provided via --vault-secret-id or $VAULT_SECRET_ID", 102);
        return token;
    }

    public HttpRequest.Builder addSecretId(HttpRequest.Builder request, String secretId) {
        return request
            .setHeader("X-AUTH-TYPE", "VAULT_APP_ROLE")
            .setHeader("X-VAULT-SECRET-ID", secretId);
    }
}
