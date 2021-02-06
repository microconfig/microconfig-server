package io.microconfig.cli.commands

import io.microconfig.cli.CliException
import io.microconfig.cli.CliFlags
import io.microconfig.server.client.ClientConfig
import io.microconfig.server.client.MicroconfigClientImpl
import io.microconfig.server.client.MicroconfigException
import io.microconfig.server.client.MicroconfigRequest
import io.microconfig.server.client.ServiceConfig
import io.microconfig.server.common.HttpException
import io.microconfig.server.common.ssl.defaultTrust
import io.microconfig.server.common.ssl.rootCa
import io.microconfig.server.common.ssl.trustAll
import java.lang.System.getenv
import java.time.Duration
import javax.net.ssl.SSLContext

abstract class Command(val args: Array<String>) {
    val flags: CliFlags = CliFlags(args)
    private val client = MicroconfigClientImpl(ClientConfig(server(), timeout(), sslContext()))

    abstract fun execute(): Int

    fun component(message: String): String {
        if (args.size < 2) throw CliException(message, 3)
        return args[1]
    }

    fun configs(component: String, type: String? = null): List<ServiceConfig> {
        try {
            val request = configsRequest(component, type)
            return client.configs(request)
        } catch (e: MicroconfigException) {
            throw CliException(e.message!!, 200)
        } catch (e: HttpException) {
            throw CliException(e.message!!, 100)
        }
    }

    private fun configsRequest(component: String, type: String?): MicroconfigRequest {
        val env = flags.env() ?: "default"
        val ref = flags.branch() ?: flags.tag() ?: flags.ref()
        return MicroconfigRequest(component, env, type, ref, flags.vars())
    }

    private fun server(): String {
        return flags.server() ?: getenv()["MCS_ADDRESS"] ?: "http://localhost:8080"
    }

    private fun timeout(): Duration {
        return Duration.ofSeconds(flags.timeout())
    }

    private fun sslContext(): SSLContext {
        return if (flags.skipTls()) trustAll() else flags.rootCa()?.let { rootCa(it) } ?: defaultTrust()
    }

}