package io.microconfig.server.git.exceptions

import io.microconfig.server.api.exceptions.NotFoundException

class BranchNotFoundException(branch: String) : NotFoundException("Git branch not found: $branch")