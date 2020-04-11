package io.microconfig.server.vault;

import io.microconfig.core.environments.Component;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.core.properties.sources.SpecialSource;
import io.microconfig.server.configs.DynamicVarsResolverStrategy;
import io.microconfig.server.vault.credentials.VaultCredentialsProvider;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.microconfig.core.properties.Property.property;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@RequiredArgsConstructor
public class VaultKVSecretResolverStrategy implements PlaceholderResolveStrategy {
    private final VaultClient vaultClient;
    private final DynamicVarsResolverStrategy dynamicVars;
    private final VaultCredentialsProvider credentialsProvider;

    @Override
    public Optional<Property> resolve(Component component, String propertyKey, String environment) {
        if (!"VAULT-KV".equals(component.getName())) return empty();
        var credentials = credentialsProvider.credentials(dynamicVars);
        String secret = vaultClient.fetchKV(credentials.getToken(dynamicVars), propertyKey);
        return of(property(propertyKey, secret, environment, new SpecialSource(component, "HashiCorp Vault")));
    }
}
