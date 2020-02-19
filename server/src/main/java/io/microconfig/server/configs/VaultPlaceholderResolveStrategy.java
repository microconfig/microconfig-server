package io.microconfig.server.configs;

import io.microconfig.configs.Property;
import io.microconfig.configs.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.configs.sources.SpecialSource;
import io.microconfig.environments.Component;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.microconfig.configs.Property.property;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@RequiredArgsConstructor
public class VaultPlaceholderResolveStrategy implements PlaceholderResolveStrategy {
    private final VaultClient vaultClient;
    private final VaultCredentials credentials;

    @Override
    public Optional<Property> resolve(Component component, String propertyKey, String environment) {
        if (!"VAULT".equals(component.getName())) return empty();

        String secret = vaultClient.fetchSecret(credentials, propertyKey);
        return of(property(propertyKey, secret, environment, new SpecialSource(component, "HashiCorp Vault")));
    }
}
