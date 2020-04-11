package io.microconfig.server.configs;

import io.microconfig.core.environments.Component;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.core.properties.sources.SpecialSource;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

import static io.microconfig.core.properties.Property.property;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@RequiredArgsConstructor
public class DynamicVarsResolverStrategy implements PlaceholderResolveStrategy {
    private final Map<String, String> vars;

    @Override
    public Optional<Property> resolve(Component component, String propertyKey, String environment) {
        if (!"this".equals(component.getName())) return empty();

        var value = vars.get(propertyKey);
        if (value == null) return empty();

        return of(property(propertyKey, value, environment, new SpecialSource(component, "Dynamic Vars")));
    }
}
