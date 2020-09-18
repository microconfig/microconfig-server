package io.microconfig.server.configs;

import io.microconfig.core.properties.DeclaringComponentImpl;
import io.microconfig.core.properties.PlaceholderResolveStrategy;
import io.microconfig.core.properties.Property;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.microconfig.core.properties.ConfigFormat.PROPERTIES;
import static io.microconfig.core.properties.PropertyImpl.property;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@RequiredArgsConstructor
public class DynamicVarsResolverStrategy implements PlaceholderResolveStrategy {
    private final Map<String, String> vars;

    public Map<String, String> dynamicVars() {
        return new HashMap<>(vars);
    }

    @Override
    public Optional<Property> resolve(String component, String key, String environment, String configType) {
        var value = vars.get(key);
        if (value == null) return empty();

        return of(property(key, value, PROPERTIES, new DeclaringComponentImpl(configType, "Dynamic Vars", environment)));
    }

}
