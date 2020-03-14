package io.microconfig.server.configs;

public class ComponentNotFoundException extends RuntimeException {

    public ComponentNotFoundException(String component) {
        super("Component not found: " + component);
    }
}
