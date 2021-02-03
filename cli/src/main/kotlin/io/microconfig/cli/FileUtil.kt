package io.microconfig.cli

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardOpenOption

object FileUtil {
    fun writeFile(file: File, content: String) {
        try {
            Files.writeString(
                file.toPath(),
                content,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE_NEW
            )
        } catch (e: IOException) {
            throw CliException("Failed to write a file ${file.absolutePath} ${e.message}", 50)
        }
    }

    fun getOrCreateDir(path: String): File {
        val outDir = File(path)
        if (!outDir.exists()) {
            try {
                Files.createDirectory(outDir.toPath())
            } catch (e: Exception) {
                throw CliException("Failed to create output directory ${outDir.absolutePath}", 51)
            }
        }
        return outDir
    }

    fun readFile(file: File): String {
        return try {
            Files.readString(file.toPath())
        } catch (e: IOException) {
            throw CliException("Failed to read a file ${file.absolutePath} ${e.message}", 52)
        }
    }
}