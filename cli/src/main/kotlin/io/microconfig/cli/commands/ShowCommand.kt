package io.microconfig.cli.commands

import io.microconfig.cli.CliException
import io.microconfig.server.common.json

class ShowCommand(args: Array<String>) : Command(args) {

    // TODO replace with /config/ call to fetch string instead of json
    override fun execute(): Int {
        val component = component(helpMessage())
        val type = flags.type() ?: "app"
        val request = request(component)
        val json = send(request).json()

        val content: String = json
            .firstOrNull { it.path("type").asText() == type }
            ?.let { it["content"].asText() }
            ?: throw CliException("No component with requested type", 404)

        println(content)
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