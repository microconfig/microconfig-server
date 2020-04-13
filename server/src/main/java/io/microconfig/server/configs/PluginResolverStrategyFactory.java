package io.microconfig.server.configs;

import io.microconfig.core.Microconfig;
import io.microconfig.core.properties.PlaceholderResolveStrategy;

public interface PluginResolverStrategyFactory {
    PlaceholderResolveStrategy strategy(Microconfig microconfig);
}
