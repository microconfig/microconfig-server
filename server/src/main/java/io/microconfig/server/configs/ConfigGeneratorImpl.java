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

import static io.microconfig.factory.configtypes.StandardConfigTypes.APPLICATION;
import static io.microconfig.factory.configtypes.StandardConfigTypes.DEPLOY;
import static io.microconfig.factory.configtypes.StandardConfigTypes.PROCESS;

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
        Stream<ConfigType> customTypes = new ConfigTypeFileProvider().getConfigTypes(configDir).stream();
        Stream<ConfigType> standardTypes = Arrays.stream(StandardConfigTypes.values()).map(StandardConfigTypes::getType);
        return Stream.concat(customTypes, standardTypes)
            .filter(t -> t.getType().equals(type))
            .findFirst()
            .orElseThrow(() -> new UnsupportedConfigTypeException(type));
    }

    @Override
    public List<ConfigResult> generateConfigs(String component, String env, String branch, PlaceholderResolveStrategy... resolvers) {
        var configDir = configDir(branch);
        var factory = init(configDir, resolvers);

        var app = generate(factory, APPLICATION.getType(), component, env);
        var process = generate(factory, PROCESS.getType(), component, env);
        var deploy = generate(factory, DEPLOY.getType(), component, env);
        return List.of(app, process, deploy);
    }

    private File configDir(String branch) {
        return branch == null ? gitService.checkoutDefault() : gitService.checkout(branch);
    }

    private MicroconfigFactory init(File configDir, PlaceholderResolveStrategy... resolvers) {
        return MicroconfigFactory.init(configDir, new File(configDir, "build"))
            .withAdditionalResolvers(List.of(resolvers));
    }

    private ConfigResult generate(MicroconfigFactory factory, ConfigType type, String component, String env) {
        ConfigProvider configProvider = factory.newConfigProvider(type);
        var properties = configProvider.getProperties(Component.byType(component), env).values();
        var file = resultFile(factory, type, component, env, properties);
        var fileContent = fileContent(factory, file, properties);
        return new ConfigResult(file.getName(), fileContent);
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
