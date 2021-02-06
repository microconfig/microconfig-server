package io.microconfig.server.client

interface MicroconfigClient {

    fun configs(request: MicroconfigRequest): List<ServiceConfig>

}