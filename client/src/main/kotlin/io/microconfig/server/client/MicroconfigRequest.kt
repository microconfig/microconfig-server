package io.microconfig.server.client

data class MicroconfigRequest(
    val component: String,
    val env: String,
    val type: String?,
    val ref: String?,
    val vars: Map<String, String> = emptyMap()
)
