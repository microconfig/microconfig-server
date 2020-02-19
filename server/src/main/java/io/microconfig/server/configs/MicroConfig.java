package io.microconfig.server.configs;

import java.util.Map;

public interface MicroConfig {
    Map<String, String> generateConfigs(String component, String env);
}
