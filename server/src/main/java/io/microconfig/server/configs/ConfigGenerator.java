package io.microconfig.server.configs;

import io.microconfig.server.vault.PluginContext;

import java.util.Map;

public interface ConfigGenerator {
    Map<String, String> generateConfigs(String component, String env, PluginContext pluginContext);
}
