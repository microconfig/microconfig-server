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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGeneratorImpl implements ConfigGenerator {
    private final GitService gitService;

    @Override
    public ConfigResult generateConfig(String component, String env, String branch, String type, PlaceholderResolveStrategy... resolvers) {
        var configDir = configDir(branch);
        var configType = configType(configDir, type);
        var factory = init(configDir, resolvers);
        return generate(factory, configType, component, env);
    }

    private ConfigType configType(File configDir, String type) {
        return types(configDir).stream()
            .filter(t -> t.getType().equals(type))
            .findFirst()
            .orElseThrow(() -> new UnsupportedConfigTypeException(type));
    }

    private List<ConfigType> types(File configDir) {
        Stream<ConfigType> customTypes = new ConfigTypeFileProvider().getConfigTypes(configDir).stream();
        Stream<ConfigType> standardTypes = Arrays.stream(StandardConfigTypes.values()).map(StandardConfigTypes::getType);
        return Stream.concat(customTypes, standardTypes).collect(toList());
    }

    @Override
    public List<ConfigResult> generateConfigs(String component, String env, String branch, PlaceholderResolveStrategy... resolvers) {
        var configDir = configDir(branch);
        var factory = init(configDir, resolvers);

        return types(configDir).stream().map(type -> generate(factory, type, component, env))
            .filter(ConfigResult::hasContent)
            .collect(toList());
    }

    private File configDir(String branch) {
        return branch == null ? gitService.checkoutDefault() : gitService.checkout(branch);
    }

    private MicroconfigFactory init(File configDir, PlaceholderResolveStrategy... resolvers) {
        var factory = MicroconfigFactory.init(configDir, new File(configDir, "build"));
        var vault = (VaultKVSecretResolverStrategy) resolvers[0];
        vault.setFactory(factory);
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
}
