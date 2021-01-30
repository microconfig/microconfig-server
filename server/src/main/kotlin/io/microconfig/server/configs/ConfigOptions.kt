package io.microconfig.server.configs

data class ConfigOptions(
    val type: String?,
    val branch: String?,
    val tag: String?,
    val vars: Map<String, String> = emptyMap()
)