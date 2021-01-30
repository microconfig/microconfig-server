package io.microconfig.server.vault.exceptions

import io.microconfig.server.api.exceptions.NotFoundException

class VaultSecretNotFound(path: String) : NotFoundException("Secret path not found: $path")