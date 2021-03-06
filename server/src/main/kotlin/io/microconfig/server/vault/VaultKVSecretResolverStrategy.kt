package io.microconfig.server.vault

import io.microconfig.core.Microconfig
import io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName
import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.DeclaringComponentImpl
import io.microconfig.core.properties.PlaceholderResolveStrategy
import io.microconfig.core.properties.Property
import io.microconfig.core.properties.PropertyImpl.property
import io.microconfig.server.common.toOptional
import java.util.*
import java.util.Optional.empty

class VaultKVSecretResolverStrategy(val microconfig: Microconfig) : PlaceholderResolveStrategy {

    private var vaultClient: VaultClient? = null

    override fun resolve(
        component: String,
        key: String,
        environment: String,
        configType: String,
        root: String
    ): Optional<Property> {
        if ("VAULT-KV" != component) return empty()
        val secret = client(environment, root, configType).fetchKV(key)

        val parent = DeclaringComponentImpl(configType, "HashiCorp Vault", environment)
        return property(key, secret, PROPERTIES, parent).toOptional()
    }

    private fun client(environment: String, root: String, configType: String): VaultClient {
        vaultClient = vaultClient ?: initClient(environment, root, configType)
        return vaultClient!!
    }

    private fun initClient(environment: String, root: String, configType: String): VaultClient {
        val properties = microconfig.inEnvironment(environment)
            .getComponentWithName(root)
            .getPropertiesFor(configTypeWithName(configType))
            .withPrefix("microconfig.vault")
            .resolveBy(microconfig.resolver())
            .propertiesAsKeyValue

        val vaultConfig = vaultConfig(properties)
        return VaultClientImpl(vaultConfig)
    }
}