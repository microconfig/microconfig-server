package io.microconfig.server.configs

import io.microconfig.core.configtypes.ConfigType
import io.microconfig.core.properties.Property
import io.microconfig.core.properties.PropertySerializer
import io.microconfig.core.properties.serializers.ConfigResult
import io.microconfig.core.templates.Template
import io.microconfig.server.client.dto.ServiceConfigMap
import io.microconfig.server.client.dto.ServiceConfigRaw
import io.microconfig.server.client.dto.ServiceTemplate

class MapSerializer : PropertySerializer<ServiceConfigMap> {
    override fun serialize(properties: Collection<Property>, templates: List<Template>, configType: ConfigType, componentName: String, environment: String): ServiceConfigMap {
        val content = properties.map { Pair(it.key, it.value) }.toMap()
        return ServiceConfigMap(
            componentName,
            configType.name,
            content,
            templates(templates)
        )
    }
}

fun asConfigMap(): PropertySerializer<ServiceConfigMap> {
    return MapSerializer()
}

fun toConfigRaw(result: ConfigResult): ServiceConfigRaw {
    return ServiceConfigRaw(
        result.component,
        result.configType,
        result.fileName,
        result.content,
        templates(result.templates)
    )
}

private fun templates(templates: List<Template>): List<ServiceTemplate> {
    return templates.map { ServiceTemplate(it.fileName, it.content) }
}
