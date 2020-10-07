package io.microconfig.server.configs;

import io.microconfig.core.properties.serializers.ConfigResult;

import java.util.List;

public interface ConfigGenerator {
    List<ConfigResult> generateConfigs(String component, String env, ConfigOptions options);
}
