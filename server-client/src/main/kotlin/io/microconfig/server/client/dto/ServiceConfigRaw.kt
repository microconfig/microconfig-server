package io.microconfig.server.client.dto

data class ServiceConfigRaw(val service: String, val type: String, val file: String, val content: String, val templates: List<ServiceTemplate>)