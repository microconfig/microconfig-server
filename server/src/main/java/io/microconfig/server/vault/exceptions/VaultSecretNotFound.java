package io.microconfig.server.vault.exceptions;

import io.microconfig.server.rest.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VaultSecretNotFound extends NotFoundException {
    private final String path;
}
