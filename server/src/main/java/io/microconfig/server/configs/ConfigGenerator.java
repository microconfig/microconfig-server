package io.microconfig.server.configs;

import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;

import java.util.List;

public interface ConfigGenerator {
    ConfigResult generateConfig(String component, String env, String branch, String type, PlaceholderResolveStrategy... resolvers);

    List<ConfigResult> generateConfigs(String component, String env, String branch, PlaceholderResolveStrategy... resolvers);
}
