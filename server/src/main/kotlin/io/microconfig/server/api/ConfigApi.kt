package io.microconfig.server.api

import io.microconfig.server.client.dto.ServiceConfigMap
import io.microconfig.server.client.dto.ServiceConfigRaw
import io.microconfig.server.configs.ConfigGenerator
import io.microconfig.server.configs.ConfigOptions
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ConfigApi(val configGenerator: ConfigGenerator) {

    @GetMapping("/configs/{component}/{env}")
    fun fetchConfigs(
        @PathVariable("component") component: String,
        @PathVariable("env") env: String,
        options: ConfigOptions
    ): List<ServiceConfigRaw> {
        return configGenerator.generateConfigs(component, env, options)

    }

    @GetMapping("/configs-map/{component}/{env}")
    fun fetchConfigsMap(
        @PathVariable("component") component: String,
        @PathVariable("env") env: String,
        options: ConfigOptions
    ): List<ServiceConfigMap> {
        return configGenerator.generateConfigMaps(component, env, options)
    }

    @GetMapping("/config/{component}/{env}")
    fun fetchConfig(
        @PathVariable("component") component: String,
        @PathVariable("env") env: String,
        options: ConfigOptions
    ): String {
        return configGenerator.generateConfigs(component, env, options).first().content
    }

}