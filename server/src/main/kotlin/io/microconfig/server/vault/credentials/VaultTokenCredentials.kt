package io.microconfig.server.vault.credentials

class VaultTokenCredentials(private val token: String) : VaultCredentials {

    override fun getToken(): String {
        return token
    }

}