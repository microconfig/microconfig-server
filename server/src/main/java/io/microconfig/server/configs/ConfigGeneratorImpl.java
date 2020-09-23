package io.microconfig.server.configs;

import io.microconfig.core.Microconfig;
import io.microconfig.core.MicroconfigRunner;
import io.microconfig.core.configtypes.ConfigTypeFilter;
import io.microconfig.core.environments.repository.EnvironmentException;
import io.microconfig.core.properties.ResolveException;
import io.microconfig.core.properties.serializers.ConfigResult;
import io.microconfig.server.git.GitService;
import io.microconfig.server.rest.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName;
import static io.microconfig.core.configtypes.ConfigTypeFilters.eachConfigType;
import static io.microconfig.core.properties.serializers.PropertySerializers.asConfigResult;
import static io.microconfig.core.properties.templates.TemplatesService.resolveTemplatesBy;
import static java.util.Collections.emptyList;

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
            log.info("Generating configs for {} in {} env", component, env);
            var microconfig = initMicroconfig(gitService, options);
            return microconfig.inEnvironment(env)
                    .findComponentsFrom(emptyList(), List.of(component))
                    .getPropertiesFor(configType)
                    .resolveBy(microconfig.resolver())
                    .forEachComponent(resolveTemplatesBy(microconfig.resolver()))
                    .save(asConfigResult());
        } catch (IllegalArgumentException | EnvironmentException | ResolveException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private Microconfig initMicroconfig(GitService gitService, ConfigOptions options) {
        var microconfig = searchConfigsIn(configDir(gitService, options));
        microconfig.logger(false);
        return withAdditionalPlaceholderResolvers(microconfig, options);
    }

    private File configDir(GitService gitService, ConfigOptions options) {
        if (options.tag != null) return gitService.checkoutTag(options.tag);
        if (options.branch != null) return gitService.checkoutBranch(options.branch);
        return gitService.checkoutDefault();
    }

    private Microconfig withAdditionalPlaceholderResolvers(Microconfig microconfig, ConfigOptions options) {
        //todo add vault and dynamic vars when strategy has root component
        var dynamicVars = new DynamicVarsResolverStrategy(options.vars);
//        var vault = new VaultKVSecretResolverStrategy(microconfig, dynamicVars);
        return microconfig.withAdditionalPlaceholderResolvers(List.of(dynamicVars));
    }
}
