package io.microconfig.server.configs;

import java.util.Map;

public interface MicroConfig {
    Map<String, String> getProperties(String component, String environment);
}
