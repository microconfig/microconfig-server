package io.microconfig.server.configs;

import java.util.List;

public interface ConfigGenerator {
    ConfigResult generateConfig(String component, String env, String type, ConfigOptions options);

    List<ConfigResult> generateConfigs(String component, String env, ConfigOptions options);
}
