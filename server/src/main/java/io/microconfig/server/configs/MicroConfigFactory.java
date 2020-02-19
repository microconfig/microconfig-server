package io.microconfig.server.configs;

import io.microconfig.configs.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.factory.ConfigType;

import java.io.File;

interface MicroConfigFactory {
    //configures api to return all possible config types for component yaml/props/xml/deploy/etc
    MicroConfig init(File rootDir, PlaceholderResolveStrategy... additionalResolvers);

    //configures api to return only selected config type
    MicroConfig init(File rootDir, ConfigType type, PlaceholderResolveStrategy... additionalResolvers);
}
