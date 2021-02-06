package io.microconfig.cli.commands

import com.fasterxml.jackson.databind.node.ArrayNode
import io.microconfig.cli.CliException
import io.microconfig.server.common.json
import java.io.File
import java.io.IOException
import java.nio.file.Files.createDirectory
import java.nio.file.Files.writeString
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE

class SaveCommand(args: Array<String>) : Command(args) {

    override fun execute(): Int {
        val component = component(helpMessage())
        val request = request(component)
        val json = send(request).json()
        val outDir = createDir(flags.dir() ?: ".")
        saveFiles(outDir, json as ArrayNode)
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

    private fun saveFiles(outDir: File, nodes: ArrayNode) {
        for (node in nodes) {
            val filename = node["fileName"].asText()
            val content = node["content"].asText()
            val file = File(outDir, filename)
            writeFile(file, content)
        }
    }

    private fun writeFile(file: File, content: String) {
        try {
            writeString(file.toPath(), content, TRUNCATE_EXISTING, WRITE, CREATE_NEW)
        } catch (e: IOException) {
            throw CliException("Failed to write a file ${file.absolutePath} ${e.message}", 50)
        }
    }

    private fun helpMessage(): String {
        return """
            Usage microctl save [component] [flags]
            Generates configuration for component and saves it to disk
            Flags: 
              -e, --env  [name]: config environment
              -t, --type [name]: config type, all types by default
              -d, --dir  [path]: output directory, current dir by default
              -s, --set  [foo=bar]: override values for placeholders and vars
            """.trimIndent()
    }
}