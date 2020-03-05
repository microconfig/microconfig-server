package io.microconfig.server.configs;

import lombok.Data;

@Data
public class ConfigResult {
    private final String fileName;
    private final String type;
    private final String content;

    public boolean hasContent() {
        return !content.isEmpty();
    }
}
