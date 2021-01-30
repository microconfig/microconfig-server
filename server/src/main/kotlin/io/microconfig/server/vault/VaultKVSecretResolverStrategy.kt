package io.microconfig.server.vault

import io.microconfig.core.Microconfig
import io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName
import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.DeclaringComponentImpl
import io.microconfig.core.properties.PlaceholderResolveStrategy
import io.microconfig.core.properties.Property
import io.microconfig.core.properties.PropertyImpl
import java.util.Optional
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
        if (vaultClient == null) {
            initClient(environment, root, configType)
        }

        val secret = vaultClient!!.fetchKV(key)

        return Optional.of(
            PropertyImpl.property(
                key, secret, PROPERTIES,
                DeclaringComponentImpl(configType, "HashiCorp Vault", environment)
            )
        )
    }

    private fun initClient(environment: String, root: String, configType: String) {
        val properties: Map<String, String> = microconfig.inEnvironment(environment)
            .getComponentWithName(root)
            .getPropertiesFor(configTypeWithName(configType))
            .withPrefix("microconfig.vault")
            .resolveBy(microconfig.resolver())
            .propertiesAsKeyValue

        vaultClient = VaultClientImpl(vaultConfig(properties))
    }
}