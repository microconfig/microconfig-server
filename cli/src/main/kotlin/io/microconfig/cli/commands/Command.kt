package io.microconfig.cli.commands

import io.microconfig.cli.CliException
import io.microconfig.cli.CliFlags
import io.microconfig.server.common.GET
import io.microconfig.server.common.HttpException
import io.microconfig.server.common.httpClient
import io.microconfig.server.common.json
import io.microconfig.server.common.send
import io.microconfig.server.common.ssl.defaultTrust
import io.microconfig.server.common.ssl.rootCa
import io.microconfig.server.common.ssl.trustAll
import java.lang.System.getenv
import java.net.http.HttpRequest
import java.time.Duration
import javax.net.ssl.SSLContext

abstract class Command(val args: Array<String>) {
    val flags: CliFlags = CliFlags(args)

    abstract fun execute(): Int

    fun component(message: String): String {
        if (args.size < 2) throw CliException(message, 3)
        return args[1]
    }

    fun request(component: String): HttpRequest {
        return GET(url(component))
            .timeout(timeout())
            .addHeaders()
    }

    private fun HttpRequest.Builder.addHeaders(): HttpRequest {
        flags.type()?.let { this.setHeader("X-TYPE", it) }
        flags.branch()?.let { this.setHeader("X-REF", it) }
        flags.tag()?.let { this.setHeader("X-REF", it) }
        flags.ref()?.let { this.setHeader("X-REF", it) }
        flags.vars().forEach { (key: String, value: String) -> this.header("X-VAR", "$key=$value") }
        return this.build()
    }

    fun send(request: HttpRequest): String {
        try {
            val client = httpClient(sslContext())
            val response = request.send(client)
            if (response.statusCode() == 200) {
                return response.body()
            } else {
                val error = response.json().get("error").asText()
                throw CliException("Server Error: $error", 200)
            }
        } catch (e: HttpException) {
            throw CliException(e.message!!, 100)
        }
    }

    private fun url(name: String): String {
        val env = flags.env() ?: "default"
        return "${server()}/api/configs/$name/$env"
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