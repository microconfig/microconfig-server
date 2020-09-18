package io.microconfig.server.configs.exceptions;

public class UnsupportedConfigTypeException extends RuntimeException {
    public UnsupportedConfigTypeException(String type) {
        super("Unsupported type: " + type);
    }
}
