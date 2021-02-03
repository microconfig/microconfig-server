package io.microconfig.cli.commands

class VersionCommand(args: Array<String>) : Command(args) {
    override fun execute(): Int {
        println("Version 0.2.0")
        return 0
    }
}