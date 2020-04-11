package io.microconfig.server.vault.exceptions;

import io.microconfig.server.rest.exceptions.ForbiddenException;

public class VaultAuthException extends ForbiddenException {
    private final String reason;

    public VaultAuthException() {
        reason = null;
    }

    public VaultAuthException(String reason) {
        this.reason = reason;
    }
}
