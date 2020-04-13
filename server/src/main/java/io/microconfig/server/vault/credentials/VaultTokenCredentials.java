package io.microconfig.server.vault.credentials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VaultTokenCredentials implements VaultCredentials {
    @Getter
    private final String token;
}