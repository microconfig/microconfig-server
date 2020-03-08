package io.microconfig.cli.util;

import io.microconfig.cli.CliException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class FileUtil {
    public static void writeFile(File file, String content) {
        try {
            Files.writeString(file.toPath(), content, TRUNCATE_EXISTING, WRITE, CREATE_NEW);
        } catch (IOException e) {
            throw new CliException("Failed to write a file " + file.getAbsolutePath() + " " + e.getMessage(), 50);
        }
    }

    public static File getOrCreateDir(String path) {
        var outDir = new File(path);
        if (!outDir.exists()) {
            try {
                createDirectory(outDir.toPath());
            } catch (Exception e) {
                throw new CliException("Failed to create output directory " + outDir.getAbsolutePath(), 51);
            }
        }
        return outDir;
    }

    public static String readFile(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new CliException("Failed to read a file " + file.getAbsolutePath() + " " + e.getMessage(), 52);
        }
    }
}
