package io.microconfig.server.configs

import io.microconfig.server.client.dto.ServiceConfigMap
import io.microconfig.server.client.dto.ServiceConfigRaw

interface ConfigGenerator {
    fun generateConfigs(component: String, env: String, options: ConfigOptions): List<ServiceConfigRaw>
    fun generateConfigMaps(component: String, env: String, options: ConfigOptions): List<ServiceConfigMap>
}