package io.microconfig.server.vault;

import io.microconfig.configs.PropertySource;
import io.microconfig.environments.Component;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.util.PropertyPlaceholderHelper;

@RequiredArgsConstructor
public class VaultResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
    private final VaultClient client;
    private final VaultCredentials credentials;

    @Override
    public String resolvePlaceholder(String placeholder) {
        if (!placeholder.startsWith("VAULT")) return null;

        return client.fetch(credentials, placeholder);
    }

    public class VaultPropertySource implements PropertySource {
        private final Component VAULT_COMPONENT = new Component("VAULT", "PLUGIN");

        @Override
        public Component getComponent() {
            return VAULT_COMPONENT;
        }

        @Override
        public String sourceInfo() {
            return "HashiCorp Vault";
        }
    }
}
