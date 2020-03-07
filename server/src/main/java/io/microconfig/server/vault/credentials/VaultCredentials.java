package io.microconfig.server.vault.credentials;

import io.microconfig.server.vault.VaultConfig;

public interface VaultCredentials {
    String getToken(VaultConfig config);
}
