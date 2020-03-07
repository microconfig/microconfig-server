package io.microconfig.server.configs;

import io.microconfig.core.environments.Component;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.core.properties.sources.SpecialSource;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.credentials.VaultCredentials;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.microconfig.core.properties.Property.property;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@RequiredArgsConstructor
public class VaultKVSecretResolverStrategy implements PlaceholderResolveStrategy {
    private final VaultClient vaultClient;
    private final VaultCredentials credentials;

    @Override
    public Optional<Property> resolve(Component component, String propertyKey, String environment) {
        if (!"VAULT-KV".equals(component.getName())) return empty();

        String secret = vaultClient.fetchSecret(credentials, propertyKey);
        return of(property(propertyKey, secret, environment, new SpecialSource(component, "HashiCorp Vault")));
    }
}
