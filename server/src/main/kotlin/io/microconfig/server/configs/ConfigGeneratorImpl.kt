package io.microconfig.server.configs

import io.microconfig.core.Microconfig
import io.microconfig.core.Microconfig.searchConfigsIn
import io.microconfig.core.configtypes.ConfigTypeFilter
import io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName
import io.microconfig.core.configtypes.ConfigTypeFilters.eachConfigType
import io.microconfig.core.properties.PlaceholderResolveStrategy
import io.microconfig.core.properties.serializers.ConfigResult
import io.microconfig.core.properties.serializers.PropertySerializers.asConfigResult
import io.microconfig.core.properties.templates.TemplatesService.resolveTemplatesBy
import io.microconfig.server.common.logger
import io.microconfig.server.git.GitService
import io.microconfig.server.vault.VaultKVSecretResolverStrategy
import org.springframework.stereotype.Service

@Service
class ConfigGeneratorImpl(val gitService: GitService) : ConfigGenerator {
    private val log = logger()

    override fun generateConfigs(component: String, env: String, options: ConfigOptions): List<ConfigResult> {
        log.info("Generating configs for {} in {} env", component, env)
        val microconfig = initMicroconfig(options)
        val configType = configTypes(options)
        return microconfig.inEnvironment(env)
            .findComponentWithName(component)
            .getPropertiesFor(configType)
            .resolveBy(microconfig.resolver())
            .forEachComponent(resolveTemplatesBy(microconfig.resolver()))
            .save(asConfigResult())
    }

    private fun initMicroconfig(options: ConfigOptions): Microconfig {
        val configDir = gitService.checkoutRef(options.ref)
        val microconfig = searchConfigsIn(configDir)
        microconfig.logger(false)
        return microconfig
            .withAdditionalPlaceholderResolvers(placeholderResolvers(microconfig, options))
    }

    private fun placeholderResolvers(microconfig: Microconfig, options: ConfigOptions): List<PlaceholderResolveStrategy> {
        val dynamicVars = DynamicVarsResolverStrategy(options.vars)
        val vault = VaultKVSecretResolverStrategy(microconfig)
        return listOf(dynamicVars, vault)
    }

    private fun configTypes(options: ConfigOptions): ConfigTypeFilter {
        return if (options.type == null) eachConfigType() else configTypeWithName(options.type)
    }

}