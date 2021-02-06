package io.microconfig.spring.client

import org.springframework.core.env.MapPropertySource

class MicroconfigPropertySource(config: Map<String, Any>) : MapPropertySource("microconfig", config)