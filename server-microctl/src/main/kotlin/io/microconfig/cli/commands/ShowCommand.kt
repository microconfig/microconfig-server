package io.microconfig.cli.commands

import io.microconfig.cli.CliException

class ShowCommand(args: Array<String>) : Command(args) {

    override fun execute(): Int {
        val component = component(helpMessage())
        val type = flags.type() ?: "app"

        configs(component, type)
            .firstOrNull { it.type == type }
            ?.let { println(it.content) }
            ?: throw CliException("No component with requested type", 404)

        return 0
    }

    private fun helpMessage(): String {
        return """
            Usage microctl show [component] [flags]
            Generates configuration for component of specified type and outputs it to console
            Flags: 
              -e, --env  [name]: config environment
              -t, --type [name]: config type, 'app' by default
              -s, --set  [foo=bar]: override values for placeholders and vars
            """.trimIndent()
    }
}