package io.microconfig.server.vault

interface VaultClient {
    fun fetchKV(property: String): String
}