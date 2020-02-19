package io.microconfig.server.configs;

import io.microconfig.core.properties.ConfigProvider;
import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.factory.ConfigType;

import java.io.File;

interface MicroConfigFactoryAdapter {
    //configures api to return all possible config types for component yaml/props/xml/deploy/etc
//    ConfigProvider init(File rootDir, PlaceholderResolveStrategy... additionalResolvers); //todo

    //configures api to return only selected config type
    ConfigProvider init(File rootDir, ConfigType type, PlaceholderResolveStrategy... additionalResolvers);
}
