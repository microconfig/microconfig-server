package io.microconfig.server.git;

import io.microconfig.server.rest.exceptions.NotFoundException;

public class BranchNotFoundException extends NotFoundException {
    public BranchNotFoundException(String branch) {
        super("Git branch not found: " + branch);
    }
}
