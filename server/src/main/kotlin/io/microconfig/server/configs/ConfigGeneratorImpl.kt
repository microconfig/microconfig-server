package io.microconfig.server.configs

import io.microconfig.core.Microconfig
import io.microconfig.core.Microconfig.searchConfigsIn
import io.microconfig.core.configtypes.ConfigTypeFilter
import io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName
import io.microconfig.core.configtypes.ConfigTypeFilters.eachConfigType
import io.microconfig.core.properties.Properties
import io.microconfig.core.properties.serializers.PropertySerializers.asConfigResult
import io.microconfig.core.properties.templates.TemplatesService.resolveTemplatesBy
import io.microconfig.server.client.dto.ServiceConfigMap
import io.microconfig.server.client.dto.ServiceConfigRaw
import io.microconfig.server.common.logger
import io.microconfig.server.git.GitService
import io.microconfig.server.vault.VaultKVSecretResolverStrategy
import org.springframework.stereotype.Service

@Service
class ConfigGeneratorImpl(val gitService: GitService) : ConfigGenerator {
    private val log = logger()

    override fun generateConfigs(component: String, env: String, options: ConfigOptions): List<ServiceConfigRaw> {
        return generateProperties(component, env, options)
            .save(asConfigResult())
            .filter { it.content.isNotEmpty() }
            .map { toConfigRaw(it) }
    }

    override fun generateConfigMaps(component: String, env: String, options: ConfigOptions): List<ServiceConfigMap> {
        return generateProperties(component, env, options)
            .save(asConfigMap())
            .filter { it.content.isNotEmpty() }
    }

    private fun generateProperties(component: String, env: String, options: ConfigOptions): Properties {
        log.info("Generating configs for {} in {} env", component, env)
        val microconfig = initMicroconfig(options)
        val configType = configTypes(options)
        return microconfig.inEnvironment(env)
            .findComponentWithName(component)
            .getPropertiesFor(configType)
            .resolveBy(microconfig.resolver())
            .forEachComponent(resolveTemplatesBy(microconfig.resolver()))
    }

    private fun initMicroconfig(options: ConfigOptions): Microconfig {
        val configDir = gitService.checkoutRef(options.ref)
        val microconfig = searchConfigsIn(configDir)
        microconfig.logger(false)
        return microconfig
            .addDynamicVars(options)
            .addVaultResolver()
    }

    private fun Microconfig.addDynamicVars(options: ConfigOptions): Microconfig {
        val dynamicVars = DynamicVarsPropertiesRepository(options.vars)
        return this.withAdditionalPropertiesRepositories(listOf(dynamicVars))
    }

    private fun Microconfig.addVaultResolver(): Microconfig {
        val vault = VaultKVSecretResolverStrategy(this)
        val resolvers = this.additionalPlaceholderResolvers() + vault
        return this.withAdditionalPlaceholderResolvers(resolvers)
    }

    private fun configTypes(options: ConfigOptions): ConfigTypeFilter {
        return if (options.type == null) eachConfigType() else configTypeWithName(options.type)
    }

}