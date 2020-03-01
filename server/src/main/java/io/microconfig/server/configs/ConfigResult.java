package io.microconfig.server.configs;

import lombok.Data;

@Data
public class ConfigResult {
    private final String fileName;
    private final String content;
}
