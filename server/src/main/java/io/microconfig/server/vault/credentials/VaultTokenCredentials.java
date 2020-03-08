package io.microconfig.server.vault.credentials;

import io.microconfig.server.vault.VaultConfig;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VaultTokenCredentials implements VaultCredentials {
    private final String token;

    @Override
    public String getToken(VaultConfig config) {
        return token;
    }
}