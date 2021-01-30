package io.microconfig.server.git.exceptions

import io.microconfig.server.api.exceptions.NotFoundException

class TagNotFoundException(tag: String) : NotFoundException("Git tag not found: $tag")