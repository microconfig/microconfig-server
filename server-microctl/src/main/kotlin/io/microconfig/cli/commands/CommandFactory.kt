package io.microconfig.cli.commands

import io.microconfig.cli.CliException

val errorMessage = """
    Usage: microctl [command] [component] [flags]
    No command provided.
    Supported commands are [show, save, version, help]
    """.trimIndent()

fun command(args: Array<String>): Command {
    if (args.isEmpty()) throw CliException(errorMessage, 1)

    return when (val command = args[0]) {
        "show" -> ShowCommand(args)
        "save" -> SaveCommand(args)
        "version" -> VersionCommand(args)
        "help" -> HelpCommand(args)
        else -> throw CliException("Unsupported argument $command", 2)
    }
}