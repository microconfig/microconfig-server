package io.microconfig.server.configs

import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.DeclaringComponentImpl
import io.microconfig.core.properties.PlaceholderResolveStrategy
import io.microconfig.core.properties.Property
import io.microconfig.core.properties.PropertyImpl.property
import io.microconfig.server.common.toOptional
import java.util.Optional
import java.util.Optional.empty

class DynamicVarsResolverStrategy(private val vars: Map<String, String>) : PlaceholderResolveStrategy {

    override fun resolve(
        component: String,
        key: String,
        environment: String,
        configType: String,
        root: String
    ): Optional<Property> {
        val value = vars[key] ?: return empty()
        val parent = DeclaringComponentImpl(configType, "Dynamic Vars", environment)
        return property(key, value, PROPERTIES, parent).toOptional()
    }
}