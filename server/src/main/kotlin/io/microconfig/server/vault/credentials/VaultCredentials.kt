package io.microconfig.server.vault.credentials

interface VaultCredentials {
    fun getToken() : String
}