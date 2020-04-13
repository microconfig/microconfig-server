package io.microconfig.server.vault;

import io.microconfig.core.Microconfig;
import io.microconfig.core.properties.DeclaringComponentImpl;
import io.microconfig.core.properties.PlaceholderResolveStrategy;
import io.microconfig.core.properties.Property;
import io.microconfig.server.configs.DynamicVarsResolverStrategy;
import lombok.RequiredArgsConstructor;

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
    public Optional<Property> resolve(String component, String key, String environment, String configType) {
        if (!"VAULT-KV".equals(component)) return empty();
        if (vaultConfig == null) {
            var properties = microconfig.inEnvironment(environment)
                .getComponentWithName(component)
                .getPropertiesFor(configTypeWithName(configType))
                .withPrefix("microconfig.vault")
                .resolveBy(microconfig.resolver())
                .getPropertiesAsKeyValue();

            properties.putAll(dynamicVars.dynamicVars());

            vaultConfig = vaultConfig(properties);
            vaultClient = new VaultClientImpl(vaultConfig);
        }

        String secret = vaultClient.fetchKV(key);
        return of(property(key, secret, PROPERTIES, new DeclaringComponentImpl(configType, "HashiCorp Vault", environment)));
    }
}
