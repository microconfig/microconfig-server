package io.microconfig.server.configs

import io.microconfig.core.Microconfig
import io.microconfig.core.configtypes.ConfigTypeFilter
import io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithName
import io.microconfig.core.configtypes.ConfigTypeFilters.eachConfigType
import io.microconfig.core.properties.serializers.ConfigResult
import io.microconfig.core.properties.serializers.PropertySerializers
import io.microconfig.core.properties.templates.TemplatesService
import io.microconfig.server.api.exceptions.BadRequestException
import io.microconfig.server.common.logger
import io.microconfig.server.git.GitService
import io.microconfig.server.vault.DynamicVarsResolverStrategy
import io.microconfig.server.vault.VaultKVSecretResolverStrategy
import org.springframework.stereotype.Service
import java.io.File

@Service
class ConfigGeneratorImpl(val gitService: GitService) : ConfigGenerator {
    private val log = logger()

    override fun generateConfigs(component: String, env: String, options: ConfigOptions): List<ConfigResult> {

        return try {
            log.info("Generating configs for {} in {} env", component, env)
            val microconfig: Microconfig = initMicroconfig(options)
            val configType: ConfigTypeFilter = configTypes(options)
            microconfig.inEnvironment(env)
                .findComponentsFrom(emptyList<String>(), listOf(component))
                .getPropertiesFor(configType)
                .resolveBy(microconfig.resolver())
                .forEachComponent(TemplatesService.resolveTemplatesBy(microconfig.resolver()))
                .save(PropertySerializers.asConfigResult())
        } catch (e: Exception) {
            throw BadRequestException(e.message)
        }
    }

    private fun initMicroconfig(options: ConfigOptions): Microconfig {
        val microconfig: Microconfig = Microconfig.searchConfigsIn(configDir(options))
        microconfig.logger(false)
        return withAdditionalPlaceholderResolvers(microconfig, options)
    }

    private fun configDir(options: ConfigOptions): File {
        if (options.tag != null) return gitService.checkoutTag(options.tag)
        return if (options.branch != null) gitService.checkoutBranch(options.branch) else gitService.checkoutDefault()
    }

    private fun withAdditionalPlaceholderResolvers(microconfig: Microconfig, options: ConfigOptions): Microconfig {
        val dynamicVars = DynamicVarsResolverStrategy(options.vars)
        val vault = VaultKVSecretResolverStrategy(microconfig)
        return microconfig.withAdditionalPlaceholderResolvers(listOf(dynamicVars, vault))
    }

    private fun configTypes(options: ConfigOptions): ConfigTypeFilter {
        return if (options.type == null) eachConfigType() else configTypeWithName(options.type)
    }

}