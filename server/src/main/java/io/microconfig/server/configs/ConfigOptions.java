package io.microconfig.server.configs;

import io.microconfig.server.vault.credentials.VaultCredentials;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
@Getter
public class ConfigOptions {
    @Nullable
    public final String branch;
    @Nullable
    public final String tag;
    @Nullable
    public final VaultCredentials vaultCredentials;
}
