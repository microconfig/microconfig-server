package io.microconfig.cli.commands

import io.microconfig.cli.CliException
import io.microconfig.server.client.dto.ServiceConfigRaw
import java.io.File
import java.io.IOException
import java.nio.file.Files.createDirectory
import java.nio.file.Files.writeString
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE

class SaveCommand(args: Array<String>) : Command(args) {

    override fun execute(): Int {
        val component = component(helpMessage())
        val configs = configs(component, flags.type())
        val outDir = createDir(flags.dir() ?: ".")

        configs.forEach { save(it, outDir) }

        return 0
    }

    private fun createDir(path: String): File {
        val outDir = File(path)
        if (!outDir.exists()) {
            try {
                createDirectory(outDir.toPath())
            } catch (e: Exception) {
                throw CliException("Failed to create output directory ${outDir.absolutePath}", 51)
            }
        }
        return outDir
    }

    private fun save(config: ServiceConfigRaw, dir: File) {
        writeFile(File(dir, config.file), config.content)

        config.templates.forEach {
            writeFile(File(dir, it.file), it.content)
        }
    }

    private fun writeFile(file: File, content: String) {
        try {
            writeString(file.toPath(), content, TRUNCATE_EXISTING, WRITE, CREATE)
        } catch (e: IOException) {
            throw CliException("Failed to write a file ${file.absolutePath} ${e.message}", 50)
        }
    }

    private fun helpMessage(): String {
        return """
            Usage microctl save [service] [flags]
            Generates configuration for service and saves it to disk
            Flags: 
              -e, --env  [name]: config environment (MC_ENV)
              -t, --type [name]: config type, all types by default (MC_TYPE)
              -d, --dir  [path]: output directory, current dir by default (MC_DIR)
              -s, --set  [foo=bar]: override values for placeholders and vars
            """.trimIndent()
    }
}