package io.microconfig.server.vault.exceptions;

import io.microconfig.server.rest.exceptions.ForbiddenException;

public class VaultAuthException extends ForbiddenException {
    public VaultAuthException(String message) {
        super(message);
    }
}
