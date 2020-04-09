package io.microconfig.server.configs;

import io.microconfig.core.environments.Component;
import io.microconfig.core.properties.ConfigProvider;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.factory.ConfigType;
import io.microconfig.factory.MicroconfigFactory;
import io.microconfig.factory.configtypes.ConfigTypeFileProvider;
import io.microconfig.factory.configtypes.StandardConfigTypes;
import io.microconfig.server.git.GitService;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultKVSecretResolverStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGeneratorImpl implements ConfigGenerator {
    private final GitService gitService;
    private final VaultClient vaultClient;

    @Override
    public ConfigResult generateConfig(String component, String env, String type, ConfigOptions options) {
        var configDir = configDir(options);
        var configType = configType(configDir, type);
        var factory = init(configDir, resolvers(options));
        var result = generate(factory, configType, component, env);
        if (!result.hasContent()) throw new ComponentNotFoundException(component);
        return result;
    }

    private ConfigType configType(File configDir, String type) {
        return types(configDir)
            .filter(t -> t.getType().equals(type))
            .findFirst()
            .orElseThrow(() -> new UnsupportedConfigTypeException(type));
    }

    private Stream<ConfigType> types(File configDir) {
        var customTypes = new ConfigTypeFileProvider().getConfigTypes(configDir);
        return customTypes.isEmpty()
            ? stream(StandardConfigTypes.values()).map(StandardConfigTypes::getType)
            : customTypes.stream();
    }

    @Override
    public List<ConfigResult> generateConfigs(String component, String env, ConfigOptions options) {
        var configDir = configDir(options);
        var factory = init(configDir, resolvers(options));

        var results = types(configDir).map(type -> generate(factory, type, component, env))
            .filter(ConfigResult::hasContent)
            .collect(toList());
        if (results.isEmpty()) throw new ComponentNotFoundException(component);
        return results;
    }

    private File configDir(ConfigOptions options) {
        if (options.tag != null) return gitService.checkoutTag(options.tag);
        return options.branch != null
            ? gitService.checkoutBranch(options.branch)
            : gitService.checkoutDefault();
    }

    private MicroconfigFactory init(File configDir, PlaceholderResolveStrategy... resolvers) {
        var factory = MicroconfigFactory.init(configDir, new File(configDir, "build"));
        return factory.withAdditionalResolvers(List.of(resolvers));
    }

    private ConfigResult generate(MicroconfigFactory factory, ConfigType type, String component, String env) {
        ConfigProvider configProvider = factory.newConfigProvider(type);
        var properties = configProvider.getProperties(Component.byType(component), env).values();
        var file = resultFile(factory, type, component, env, properties);
        var fileContent = fileContent(factory, file, properties);
        return new ConfigResult(file.getName(), type.getType(), fileContent);
    }

    private File resultFile(MicroconfigFactory factory, ConfigType type, String component, String env, Collection<Property> properties) {
        return factory.getFilenameGenerator(type)
            .fileFor(component, env, properties);
    }

    private String fileContent(MicroconfigFactory factory, File file, Collection<Property> properties) {
        return factory.getConfigIoService()
            .writeTo(file)
            .serialize(properties);
    }

    private PlaceholderResolveStrategy[] resolvers(ConfigOptions options) {
        return options.vaultCredentials != null
            ? new PlaceholderResolveStrategy[]{new VaultKVSecretResolverStrategy(vaultClient, options.vaultCredentials)}
            : new PlaceholderResolveStrategy[0];
    }
}
