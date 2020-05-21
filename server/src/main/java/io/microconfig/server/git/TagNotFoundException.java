package io.microconfig.server.git;

import io.microconfig.server.rest.exceptions.NotFoundException;

public class TagNotFoundException extends NotFoundException {
    public TagNotFoundException(String tag) {
        super("Git tag not found: " + tag);
    }
}
