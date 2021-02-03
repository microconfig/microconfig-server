package io.microconfig.server.configs

data class ConfigOptions(
    val type: String?,
    val ref: String?,
    val vars: Map<String, String> = emptyMap()
)