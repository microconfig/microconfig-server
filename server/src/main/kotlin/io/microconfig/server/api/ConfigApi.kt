package io.microconfig.server.api

import io.microconfig.core.properties.serializers.ConfigResult
import io.microconfig.core.templates.Template
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
    ): List<ConfigsResponse> {
        return configGenerator
            .generateConfigs(component, env, options)
            .asSequence()
            .filter { it.content.isNotEmpty() }
            .flatMap { map(it) }
            .toList()
    }

    data class ConfigsResponse(val service: String, val type: String, val fileName: String, val content: String)

    private fun map(result: ConfigResult): List<ConfigsResponse> {
        val resp = ConfigsResponse(
            result.component,
            result.configType,
            result.fileName,
            result.content
        )
        val files = templates(result.component, result.templates)
        files.add(resp)
        return files
    }

    private fun templates(component: String, templates: List<Template>): MutableList<ConfigsResponse> {
        return templates
            .map { ConfigsResponse(component, "template", it.fileName, it.content) }
            .toMutableList()
    }

}