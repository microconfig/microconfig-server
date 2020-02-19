package io.microconfig.server.configs;

import io.microconfig.core.environments.Component;
import io.microconfig.core.properties.ConfigProvider;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

import static io.microconfig.core.properties.Property.asStringMap;
import static io.microconfig.factory.configtypes.StandardConfigTypes.APPLICATION;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ConfigGeneratorImpl implements ConfigGenerator {
    private final File gitRootDir;
    private final VaultClient vaultClient;
    private final MicroConfigFactoryAdapter microConfigFactoryAdapter;

    public ConfigGeneratorImpl(@Value("${git.localDir:}") File gitRootDir,
                               VaultClient vaultClient,
                               MicroConfigFactoryAdapter microConfigFactoryAdapter) {
        this.gitRootDir = gitRootDir;
        this.vaultClient = vaultClient;
        this.microConfigFactoryAdapter = microConfigFactoryAdapter;
    }

    @Override
    public Map<String, String> generateConfigs(String component, String env, VaultCredentials vaultCredentials) {
        ConfigProvider configProvider = initConfigProvider(vaultCredentials);
        return asStringMap(configProvider.getProperties(Component.byType(component), env));
    }

    private ConfigProvider initConfigProvider(VaultCredentials vaultCredentials) {
        return microConfigFactoryAdapter.init(
                gitRootDir,
                APPLICATION.getType(),
                new VaultPlaceholderResolveStrategy(vaultClient, requireNonNull(vaultCredentials))
        );
    }
}
