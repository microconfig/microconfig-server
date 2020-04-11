package io.microconfig.server.vault;

import io.microconfig.server.configs.DynamicVarsResolverStrategy;
import io.microconfig.server.vault.credentials.VaultCredentialsProvider;

import java.util.Optional;

public class VaultPlugin {

    //todo make microconfig api as param instead of strategy
    public static Optional<VaultKVSecretResolverStrategy> vaultKVSecretResolverStrategy(DynamicVarsResolverStrategy vars) {
        var config = config(vars);
        if (config.isEmpty()) return Optional.empty();
        var client = new VaultClientImpl(config.get());
        var strategy = new VaultKVSecretResolverStrategy(client, vars, new VaultCredentialsProvider(config.get()));
        return Optional.of(strategy);
    }

    private static Optional<VaultConfig> config(DynamicVarsResolverStrategy dynamicVarsResolverStrategy) {
        var address = dynamicVarsResolverStrategy.findValue("microconfig.vault.address");
        if (address.isPresent()) {
            var credentials = dynamicVarsResolverStrategy.getValue("microconfig.vault.auth");
            var config = new VaultConfig(address.get(), credentials);
            return Optional.of(config);
        } else {
            return Optional.empty();
        }
    }
}
