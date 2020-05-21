package io.microconfig.server.vault.exceptions;

import io.microconfig.server.rest.exceptions.NotFoundException;

public class VaultSecretNotFound extends NotFoundException {
    public VaultSecretNotFound(String path) {
        super("Secret path not found: " + path);
    }
}
