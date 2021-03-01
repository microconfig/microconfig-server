package io.microconfig.server.client.dto

data class ServiceConfigMap(val service: String, val type: String, val content: Map<String, Any>, val templates: List<ServiceTemplate>)