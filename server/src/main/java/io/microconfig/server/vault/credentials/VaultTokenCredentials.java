package io.microconfig.server.vault.credentials;

import io.microconfig.server.vault.VaultConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class VaultTokenCredentials implements VaultCredentials {
    private final String token;

    @Override
    public String getToken(VaultConfig config) {
        return token;
    }
}