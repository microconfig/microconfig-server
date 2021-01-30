package io.microconfig.server.git

import java.io.File

interface GitService {
    fun checkoutDefault(): File
    fun checkoutBranch(branch: String): File
    fun checkoutTag(tag: String): File
}