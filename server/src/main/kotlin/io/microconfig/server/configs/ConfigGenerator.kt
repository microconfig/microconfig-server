package io.microconfig.server.configs

import io.microconfig.core.properties.serializers.ConfigResult

interface ConfigGenerator {
    fun generateConfigs(component: String, env: String, options: ConfigOptions): List<ConfigResult>
}