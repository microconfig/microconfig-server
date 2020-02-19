package io.microconfig.server.configs;

import io.microconfig.server.git.GitService;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.microconfig.factory.configtypes.StandardConfigTypes.APPLICATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGeneratorImpl implements ConfigGenerator {
    private final GitService gitService;
    private final VaultClient vaultClient;
    private final MicroConfigFactory microConfigFactory;

    @Override
    public Map<String, String> generateConfigs(String component, String env, VaultCredentials vaultCredentials) {
        MicroConfig mc = microConfigFactory.init(gitService.getLocalDir(), APPLICATION.getType(), new VaultPlaceholderResolveStrategy(vaultClient, vaultCredentials));
        return mc.getProperties(component, env);
    }
}
