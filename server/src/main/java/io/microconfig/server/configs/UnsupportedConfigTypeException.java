package io.microconfig.server.configs;

public class UnsupportedConfigTypeException extends RuntimeException {
    public UnsupportedConfigTypeException(String type) {
        super("Unsupported type: " + type);
    }
}
