package io.microconfig.server.git

import java.io.File

interface GitService {
    fun checkoutRef(ref: String? = null): File
}