package io.microconfig.cli.commands

class HelpCommand(args: Array<String>) : Command(args) {
    private val message = """
        Usage: microctl [command] [component] [flags]
        Commands:
          show:     fetch config and show it in console
          save:     fetch config and save it to disk
          version:  show cli version
          help:     show this message
        Common Flags:
          --ref [master]: git branch or tag to use, by default master
          --timeout [10]: server calls timeout in seconds, by default 10
          --server [url]:  server url (or MCS_ADDRESS env variable), by default [http://localhost:8080]
          --skip-tls: skip server tls certificate verification
          --tls-root-ca [path]: expected Root CA certificate
        """.trimIndent()

    override fun execute(): Int {
        println(message)
        return 0
    }
}