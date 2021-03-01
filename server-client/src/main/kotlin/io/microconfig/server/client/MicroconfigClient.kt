package io.microconfig.server.client

import io.microconfig.server.client.dto.ServiceConfigMap
import io.microconfig.server.client.dto.ServiceConfigRaw

interface MicroconfigClient {

    fun configs(request: MicroconfigRequest): List<ServiceConfigRaw>
    fun configMaps(request: MicroconfigRequest): List<ServiceConfigMap>

}