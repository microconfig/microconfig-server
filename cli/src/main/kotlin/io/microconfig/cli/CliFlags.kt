package io.microconfig.cli

import java.util.stream.IntStream.range
import kotlin.streams.asSequence

class CliFlags(private val args: Array<String>) {
    fun env(): String? {
        return findFlag("-e", "--env")
    }

    fun dir(): String? {
        return findFlag("-d", "--dir")
    }

    fun vars(): Map<String, String> {
        return findVars("-s", "--set")
    }

    fun branch(): String? {
        return findFlag("--branch")
    }

    fun tag(): String? {
        return findFlag("--tag")
    }

    fun ref(): String? {
        return findFlag("--ref")
    }

    fun timeout(): Long {
        return findFlag("--timeout")?.toLong() ?: 10
    }

    fun server(): String? {
        return findFlag("--server")
    }

    fun skipTls(): Boolean {
        return args.contains("--skip-tls")
    }

    fun rootCa(): String? {
        return findFlag("--tls-root-ca")
    }

    fun type(): String? {
        return findFlag("-t", "--type")
    }

    private fun findFlag(vararg flags: String): String? {
        for (i in 2 until args.size) {
            val arg = args[i]
            if (flags.contains(arg)) {
                if (i == args.size - 1) throw CliException("$arg value not provided", 4)
                return args[i + 1]
            }
        }
        return null
    }

    private fun findVars(vararg flags: String): Map<String, String> {
        return range(0, args.size).asSequence()
            .filter { flags.contains(args[it]) }
            .onEach { validate(it) }
            .map { splitVar(args[it + 1]) }
            .toMap()
    }

    private fun validate(i: Int) {
        if (i == args.size - 1) throw CliException("--set doesn't have a value", 4)
        val value = args[i + 1]
        if (value.startsWith('-')) throw CliException("--set doesn't have a value", 4)
        if (!value.contains('=')) throw CliException("--set value should contain '=' $value", 4)
    }

    private fun splitVar(str: String): Pair<String, String> {
        val idx = str.indexOf('=')
        if (idx == str.length - 1) throw CliException("No value after '=' in $str", 4)
        val key = str.substring(0, idx)
        val value = str.substring(idx + 1)
        return Pair(key, value)
    }
}