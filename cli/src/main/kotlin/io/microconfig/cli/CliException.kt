package io.microconfig.cli

class CliException(message: String, val exitCode: Int) : RuntimeException(message)