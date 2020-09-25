package io.microconfig.server.vault;

import io.microconfig.core.Microconfig;
import io.microconfig.core.properties.DeclaringComponentImpl;
import io.microconfig.core.properties.Placeholder;
import io.microconfig.core.properties.PlaceholderResolveStrategy;
import io.microconfig.core.properties.Property;
import io.microconfig.server.configs.DynamicVarsResolverStrategy;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Optional;

import static io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName;
import static io.microconfig.core.properties.ConfigFormat.PROPERTIES;
import static io.microconfig.core.properties.PropertyImpl.property;
import static io.microconfig.server.vault.VaultConfig.vaultConfig;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@RequiredArgsConstructor
public class VaultKVSecretResolverStrategy implements PlaceholderResolveStrategy {
    private final Microconfig microconfig;
    private final DynamicVarsResolverStrategy dynamicVars;
    private VaultConfig vaultConfig;
    private VaultClient vaultClient;

    @Override
    public Optional<Property> resolve(Placeholder placeholder) {
        if (!"VAULT-KV".equals(placeholder.getComponent())) return empty();
        if (vaultConfig == null) {
            var properties = microconfig.inEnvironment(placeholder.getEnvironment())
                .getComponentWithName(placeholder.getRootComponent())
                .getPropertiesFor(configTypeWithName(placeholder.getConfigType()))
                .withPrefix("microconfig.vault")
                .resolveBy(microconfig.resolver())
                .getPropertiesAsKeyValue();

            var vars = new HashMap<>(properties);
            vars.putAll(dynamicVars.dynamicVars());

            vaultConfig = vaultConfig(vars);
            vaultClient = new VaultClientImpl(vaultConfig);
        }

        String secret = vaultClient.fetchKV(placeholder.getKey());
        return of(property(placeholder.getKey(), secret, PROPERTIES,
                new DeclaringComponentImpl(placeholder.getConfigType(), "HashiCorp Vault", placeholder.getEnvironment())));
    }
}
