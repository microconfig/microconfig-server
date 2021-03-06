package io.microconfig.server.configs

import io.microconfig.core.configtypes.ConfigType
import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.DeclaringComponentImpl
import io.microconfig.core.properties.PropertiesRepository
import io.microconfig.core.properties.Property
import io.microconfig.core.properties.PropertyImpl.varProperty

class DynamicVarsPropertiesRepository(private val vars: Map<String, String>) : PropertiesRepository {

    override fun getPropertiesOf(
        originalComponentName: String,
        environment: String,
        configType: ConfigType
    ): Map<String, Property> {
        val parent = DeclaringComponentImpl(configType.name, "Dynamic Vars", environment)
        return vars
            .map { (k, v) -> k to varProperty(k, v, PROPERTIES, parent) }
            .toMap()
    }
}