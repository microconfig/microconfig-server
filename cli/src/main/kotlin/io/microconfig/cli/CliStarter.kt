package io.microconfig.cli

import io.microconfig.cli.commands.command
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        val code = command(args).execute()
        exitProcess(code)
    } catch (e: CliException) {
        println(e.message)
        exitProcess(e.exitCode)
    }
}