package io.microconfig.cli.commands

class HelpCommand(args: Array<String>) : Command(args) {
    private val message = """
        Usage: microctl [command] [service] [flags]
        Commands:
          show:     fetch config and show it in console (MC_CMD=show)
          save:     fetch config and save it to disk (MC_CMD=save)
          version:  show cli version
          help:     show this message
        Service:
          Generate configs for this service name (MC_SERVICE)
        Common Flags:
          --ref [master]: git branch or tag to use, by default master (MC_REF)
          --timeout [10]: server calls timeout in seconds, by default 10 (MC_TIMEOUT)
          --server [url]:  server url, by default [http://localhost:8080] (MC_ADDRESS)
          --skip-tls: skip server tls certificate verification (MC_SKIP_TLS=true)
          --tls-root-ca [path]: expected Root CA certificate (MC_ROOT_CA)
        """.trimIndent()

    override fun execute(): Int {
        println(message)
        return 0
    }
}