package io.microconfig.spring.client;

import org.springframework.core.env.MapPropertySource;

import java.util.Map;

public class MicroconfigPropertySource extends MapPropertySource {
    public MicroconfigPropertySource() {
        super("microconfig", Map.of("server.port", "8081", "micro", "config"));
    }
}
