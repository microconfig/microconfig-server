package io.microconfig.server.git.exceptions

import io.microconfig.server.api.exceptions.NotFoundException

class RefNotFound(ref: String) : NotFoundException("Git ref not found: $ref")