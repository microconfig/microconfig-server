package io.microconfig.server.vault;

import org.springframework.util.PropertyPlaceholderHelper;

public interface VaultClient {
    String fetchSecret(VaultCredentials credentials, String placeholder);

    PropertyPlaceholderHelper.PlaceholderResolver asResolver(VaultCredentials credentials);
}
