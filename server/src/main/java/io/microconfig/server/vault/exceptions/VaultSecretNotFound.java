package io.microconfig.server.vault.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VaultSecretNotFound extends VaultException {
    private final String path;
}
