package io.microconfig.server.vault;

import io.microconfig.server.vault.credentials.VaultCredentials;

public interface VaultClient {
    String fetchSecret(VaultCredentials credentials, String property);
}
