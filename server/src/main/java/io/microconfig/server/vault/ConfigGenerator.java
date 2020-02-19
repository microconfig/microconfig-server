package io.microconfig.server.vault;

import io.microconfig.factory.ConfigType;
import io.microconfig.server.git.GitService;
import lombok.Data;
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
    private final MicroconfigFactoryUpdate factory;

    public List<GeneratedConfig> generateConfig(String component, String env, PluginContext pluginContext) {
        var resolvers = resolvers(pluginContext);
        var api = factory.init(gitService.getLocal(), resolvers, APPLICATION.getType());

        return api.generate(component, env);
    }

    private Map<String, PlaceholderResolver> resolvers(PluginContext pluginContext) {
        if (pluginContext instanceof VaultCredentials) {
            var credentials = (VaultCredentials) pluginContext;
            var vaultResolver = vaultClient.resolver(credentials);
            return Map.of("VAULT", vaultResolver);
        }

        return emptyMap();
    }

    @Data
    public static class GeneratedConfig {
        private final String name;
        private final String content;
    }

    public interface MicroconfigApi {
        List<GeneratedConfig> generate(String component, String env);
    }

    private interface MicroconfigFactoryUpdate {
        //configures api to return all possible config types for component yaml/props/xml/deploy/etc
        MicroconfigApi init(File rootDir, Map<String, PlaceholderResolver> resolvers);

        //configures api to return only selected config type
        MicroconfigApi init(File rootDir, Map<String, PlaceholderResolver> resolvers, ConfigType type);
    }
}
