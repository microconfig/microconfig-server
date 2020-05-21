package io.microconfig.server.configs;

import io.microconfig.core.Microconfig;
import io.microconfig.core.configtypes.ConfigTypeFilter;
import io.microconfig.core.environments.repository.EnvironmentException;
import io.microconfig.core.properties.serializers.ConfigResult;
import io.microconfig.server.git.GitService;
import io.microconfig.server.rest.exceptions.BadRequestException;
import io.microconfig.server.vault.VaultKVSecretResolverStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName;
import static io.microconfig.core.configtypes.ConfigTypeFilters.eachConfigType;
import static io.microconfig.core.properties.serializers.PropertySerializers.asConfigResult;
import static java.util.Arrays.asList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGeneratorImpl implements ConfigGenerator {
    private final GitService gitService;

    @Override
    public ConfigResult generateConfig(String component, String env, String configType, ConfigOptions options) {
        return generateConfigsFor(configTypeWithName(configType), component, env, options).get(0);
    }

    @Override
    public List<ConfigResult> generateConfigs(String component, String env, ConfigOptions options) {
        return generateConfigsFor(eachConfigType(), component, env, options);
    }

    private List<ConfigResult> generateConfigsFor(ConfigTypeFilter configType, String component, String env, ConfigOptions options) {
        try {
            var microconfig = initMicroconfig(gitService, options);
            return microconfig.inEnvironment(env)
                .getComponentWithName(component)
                .getPropertiesFor(configType)
                .resolveBy(microconfig.resolver())
                .save(asConfigResult());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (EnvironmentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private Microconfig initMicroconfig(GitService gitService, ConfigOptions options) {
        var microconfig = searchConfigsIn(configDir(gitService, options));
        return withAdditionalPlaceholderResolvers(microconfig, options);
    }

    private File configDir(GitService gitService, ConfigOptions options) {
        if (options.tag != null) return gitService.checkoutTag(options.tag);
        if (options.branch != null) return gitService.checkoutBranch(options.branch);
        return gitService.checkoutDefault();
    }

    private Microconfig withAdditionalPlaceholderResolvers(Microconfig microconfig, ConfigOptions options) {
        var dynamicVars = new DynamicVarsResolverStrategy(options.vars);
        var vault = new VaultKVSecretResolverStrategy(microconfig, dynamicVars);
        return microconfig.withAdditionalPlaceholderResolvers(asList(dynamicVars, vault));
    }
}
