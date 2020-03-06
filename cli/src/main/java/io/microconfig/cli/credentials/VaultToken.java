package io.microconfig.cli.credentials;

import io.microconfig.cli.CliException;
import io.microconfig.cli.CliFlags;

import java.net.http.HttpRequest;

public class VaultToken implements Credentials {

    @Override
    public HttpRequest.Builder addCredentials(HttpRequest.Builder request, String[] args) {
        var flags = new CliFlags(args);
        var token = flags.vaultToken().orElseGet(this::fromEnv);
        return addToken(request, token);
    }

    private String fromEnv() {
        var token = System.getenv("VAULT_TOKEN");
        if (token == null || token.isEmpty())
            throw new CliException("Vault token not provided via --vault-token or $VAULT_TOKEN", 101);
        return token;
    }

    public HttpRequest.Builder addToken(HttpRequest.Builder request, String token) {
        return request
            .setHeader("X-AUTH-TYPE", "VAULT_TOKEN")
            .setHeader("X-VAULT-TOKEN", token);
    }
}
