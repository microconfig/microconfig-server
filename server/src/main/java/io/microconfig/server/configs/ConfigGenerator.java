package io.microconfig.server.configs;

import io.microconfig.factory.ConfigType;
import io.microconfig.server.git.GitService;
import io.microconfig.server.vault.PluginContext;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.microconfig.factory.configtypes.StandardConfigTypes.APPLICATION;
import static java.util.Collections.emptyMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGenerator {
    private final GitService gitService;
    private final VaultClient vaultClient;
    private final MicroConfigFactoryUpdate factory;

    public List<GeneratedConfig> generateConfig(String component, String env, PluginContext pluginContext) {
        var resolvers = resolvers(pluginContext);
        var api = factory.init(gitService.getLocalDir(), resolvers, APPLICATION.getType());

        return api.generate(component, env);
    }

    private Map<String, PlaceholderResolver> resolvers(PluginContext pluginContext) {
        if (pluginContext instanceof VaultCredentials) {
            var credentials = (VaultCredentials) pluginContext;
            var vaultResolver = vaultClient.asResolver(credentials);
            return Map.of("VAULT", vaultResolver);
        }

        return emptyMap();
    }

    public interface MicroConfigApi {
        List<GeneratedConfig> generate(String component, String env);
    }

    private interface MicroConfigFactoryUpdate {
        //configures api to return all possible config types for component yaml/props/xml/deploy/etc
        MicroConfigApi init(File rootDir, Map<String, PlaceholderResolver> resolvers);

        //configures api to return only selected config type
        MicroConfigApi init(File rootDir, Map<String, PlaceholderResolver> resolvers, ConfigType type);
    }
}
