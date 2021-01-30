package io.microconfig.server.vault.exceptions

import io.microconfig.server.api.exceptions.ForbiddenException

class VaultAuthException(message: String) : ForbiddenException(message)