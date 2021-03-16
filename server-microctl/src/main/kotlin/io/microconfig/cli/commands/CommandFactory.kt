package io.microconfig.cli.commands

import io.microconfig.cli.CliException

val errorMessage = """
    Usage: microctl [command] [service] [flags]
    No command provided.
    Supported commands are [show, save, version, help]
    """.trimIndent()

fun command(args: Array<String>): Command {
    val cmd = System.getenv()["MC_CMD"]
    if (args.isEmpty() && cmd == null) throw CliException(errorMessage, 1)

    return when (val command = args.firstOrNull() ?: cmd) {
        "show" -> ShowCommand(args)
        "save" -> SaveCommand(args)
        "version" -> VersionCommand(args)
        "help" -> HelpCommand(args)
        else -> throw CliException("Unsupported argument $command", 2)
    }
}