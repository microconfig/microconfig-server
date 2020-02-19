package io.microconfig.server.configs;

import io.microconfig.factory.ConfigType;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.File;
import java.util.Map;

interface MicroConfigFactory {
    //configures api to return all possible config types for component yaml/props/xml/deploy/etc
    MicroConfig init(File rootDir, Map<String, PropertyPlaceholderHelper.PlaceholderResolver> resolvers);

    //configures api to return only selected config type
    MicroConfig init(File rootDir, Map<String, PropertyPlaceholderHelper.PlaceholderResolver> resolvers, ConfigType type);
}
