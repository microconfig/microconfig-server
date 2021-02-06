package io.microconfig.server.client

data class MicroconfigRequest(
    val component: String,
    val env: String,
    val type: String? = null,
    val ref: String? = null,
    val vars: Map<String, String> = emptyMap()
)
