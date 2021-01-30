package io.microconfig.server.vault

import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.DeclaringComponentImpl
import io.microconfig.core.properties.PlaceholderResolveStrategy
import io.microconfig.core.properties.Property
import io.microconfig.core.properties.PropertyImpl
import java.util.HashMap
import java.util.Optional
import java.util.Optional.empty

class DynamicVarsResolverStrategy(private val vars: Map<String, String>) : PlaceholderResolveStrategy {

    fun dynamicVars(): Map<String, String> {
        return HashMap(vars)
    }

    override fun resolve(
        component: String,
        key: String,
        environment: String,
        configType: String,
        root: String
    ): Optional<Property> {
        return vars[key]?.let {
            Optional.of(
                PropertyImpl.property(
                    key, it, PROPERTIES,
                    DeclaringComponentImpl(configType, "Dynamic Vars", environment)
                )
            )
        } ?: empty()
    }
}