package io.microconfig.server.configs;

import io.microconfig.server.git.GitService;
import io.microconfig.server.vault.PluginContext;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;

import static io.microconfig.factory.configtypes.StandardConfigTypes.APPLICATION;
import static java.util.Collections.emptyMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGeneratorImpl implements ConfigGenerator {
    private final GitService gitService;
    private final VaultClient vaultClient;
    private final MicroConfigFactory microConfigFactory;

    @Override
    public Map<String, String> generateConfigs(String component, String env, PluginContext pluginContext) {
        var resolvers = resolvers(pluginContext);

        MicroConfig mc = microConfigFactory.init(gitService.getLocalDir(), resolvers, APPLICATION.getType());
        return mc.getProperties(component, env);
    }

    private Map<String, PropertyPlaceholderHelper.PlaceholderResolver> resolvers(PluginContext pluginContext) {
        if (pluginContext instanceof VaultCredentials) {
            var credentials = (VaultCredentials) pluginContext;
            var vaultResolver = vaultClient.asResolver(credentials);
            return Map.of("VAULT", vaultResolver);
        }

        return emptyMap();
    }
}
