package io.microconfig.server.configs;

import io.microconfig.server.rest.exceptions.NotFoundException;

public class ComponentNotFoundException extends NotFoundException {

    public ComponentNotFoundException(String component) {
        super("Component not found: " + component);
    }
}
