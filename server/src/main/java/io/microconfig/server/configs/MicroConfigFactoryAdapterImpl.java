package io.microconfig.server.configs;

import io.microconfig.core.properties.ConfigProvider;
import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.factory.ConfigType;
import io.microconfig.factory.MicroconfigFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class MicroConfigFactoryAdapterImpl implements MicroConfigFactoryAdapter {
    @Override
    public ConfigProvider init(File rootDir, ConfigType type, PlaceholderResolveStrategy... additionalResolvers) {
        return MicroconfigFactory.init(rootDir, new File(rootDir, "build"))
                .withAdditionalResolvers(List.of(additionalResolvers))
                .newConfigProvider(type);
    }
}