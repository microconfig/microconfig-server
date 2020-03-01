package io.microconfig.server.configs;

import io.microconfig.core.environments.Component;
import io.microconfig.core.properties.ConfigProvider;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.factory.ConfigType;
import io.microconfig.factory.MicroconfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static io.microconfig.factory.configtypes.StandardConfigTypes.APPLICATION;
import static io.microconfig.factory.configtypes.StandardConfigTypes.PROCESS;

@Slf4j
@Service
public class ConfigGeneratorImpl implements ConfigGenerator {
    private final File gitRootDir;

    public ConfigGeneratorImpl(@Value("${git.localDir:}") File gitRootDir) {
        this.gitRootDir = gitRootDir;
    }

    @Override
    public List<ConfigResult> generateConfigs(String component, String env, PlaceholderResolveStrategy... resolvers) {
        var factory = init(resolvers);

        var app = generate(factory, APPLICATION.getType(), component, env);
        var process = generate(factory, PROCESS.getType(), component, env);
        return List.of(app, process);
    }

    private MicroconfigFactory init(PlaceholderResolveStrategy... resolvers) {
        return MicroconfigFactory.init(gitRootDir, new File(gitRootDir, "build"))
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
