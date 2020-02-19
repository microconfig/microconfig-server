package io.microconfig.server.vault;

public interface VaultClient {
    String fetchSecret(VaultCredentials credentials, String property);
}
