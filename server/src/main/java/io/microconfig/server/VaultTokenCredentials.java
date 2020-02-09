package io.microconfig.server;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class VaultTokenCredentials implements VaultCredentials {
    private final String token;

    @Override
    public Vault insert(VaultConfig config) {
        return new Vault(config.token(token));
    }
}
